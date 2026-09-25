package com.example.ChaoticDeck.Service;

import com.example.ChaoticDeck.Model.BOMB.BOMB;
import com.example.ChaoticDeck.Model.HandCard.HandCard;
import com.example.ChaoticDeck.Model.RoomData.RoomData;
import com.example.ChaoticDeck.Model.TOP3.TOP3;
import com.example.ChaoticDeck.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Service
public class GameService {

    private final BombRepository bombRepository;
    private final DeckListRepository deckListRepository;
    private final HandCardRepository handCardRepository;
    private final RoomDataRepository roomDataRepository;
    private final Top3Repository top3Repository;
    private final PlayerinRoomRepository playerinRoomRepository;
    private final SimpMessagingTemplate messagingTemplate;

    private final Map<String, PendingAction> pendingActions = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);

    public GameService(BombRepository bombRepository, 
                       DeckListRepository deckListRepository,
                       HandCardRepository handCardRepository,
                       RoomDataRepository roomDataRepository,
                       Top3Repository top3Repository,
                       PlayerinRoomRepository playerinRoomRepository,
                       SimpMessagingTemplate messagingTemplate) {
        this.bombRepository = bombRepository;
        this.deckListRepository = deckListRepository;
        this.handCardRepository = handCardRepository;
        this.roomDataRepository = roomDataRepository;
        this.top3Repository = top3Repository;
        this.playerinRoomRepository = playerinRoomRepository;
        this.messagingTemplate = messagingTemplate;
    }

    class PendingAction {
        String roomId;
        long playerId;
        String cardType;
        Long targetPlayerId;
        int nopeCount = 0;
        ScheduledFuture<?> timeoutTask;
    }

    public void startGame(String roomId) {
        List<Long> players = playerinRoomRepository.getPlayerIdsInRoom(roomId);
        int playerCount = players.size();
        
        for (Long pid : players) {
            playerinRoomRepository.updatePlayerStatus(roomId, pid, "ALIVE");
        }
        
        for (Long playerId : players) {
            handCardRepository.addOrUpdateCard(playerId, 2); 
            for (int i=0; i<4; i++) {
                int randomCardId = new Random().nextInt(10) + 3; 
                handCardRepository.addOrUpdateCard(playerId, randomCardId);
            }
        }
        
        int bombCount = playerCount - 1;
        for (int i = 0; i < bombCount; i++) {
            bombRepository.add(new BOMB(roomId, -1));
        }
        
        for(int cardId=3; cardId<=13; cardId++) {
             int amount = (cardId == 13) ? 5 : 4; // Add 5 NOPEs
             deckListRepository.addCardToDeck(roomId, cardId, amount);
        }

        roomDataRepository.updateTurnCount(roomId, 0);
        roomDataRepository.updateRequiredDraws(roomId, 1);
        roomDataRepository.updateStatus(roomId, "PLAYING");

        messagingTemplate.convertAndSend("/topic/room/" + roomId, "GAME_STARTED");
    }

    public long getCurrentPlayerTurn(String roomId) {
        RoomData room = roomDataRepository.findByRoomId(roomId);
        List<Long> allPlayers = playerinRoomRepository.getPlayerIdsInRoom(roomId);
        
        int turnCount = room.getTurnCount();
        int max = allPlayers.size();
        
        for (int i=0; i<max; i++) {
            long pId = allPlayers.get(turnCount % max);
            String status = playerinRoomRepository.getPlayerStatus(roomId, pId);
            if (status != null && !status.equals("DEAD")) {
                if (turnCount != room.getTurnCount()) {
                     roomDataRepository.updateTurnCount(roomId, turnCount);
                }
                return pId;
            }
            turnCount++;
        }
        return -1; 
    }

    public void eliminatePlayer(String roomId, long playerId) {
        playerinRoomRepository.updatePlayerStatus(roomId, playerId, "DEAD");
        messagingTemplate.convertAndSend("/topic/room/" + roomId, "PLAYER_ELIMINATED:" + playerId);
        
        List<Long> alivePlayers = playerinRoomRepository.getAlivePlayerIdsInRoom(roomId);
        if (alivePlayers.size() <= 1) {
            roomDataRepository.updateStatus(roomId, "FINISHED");
            if (alivePlayers.size() == 1) {
                messagingTemplate.convertAndSend("/topic/room/" + roomId, "GAME_OVER:WINNER:" + alivePlayers.get(0));
            } else {
                messagingTemplate.convertAndSend("/topic/room/" + roomId, "GAME_OVER:DRAW");
            }
        } else {
            RoomData room = roomDataRepository.findByRoomId(roomId);
            roomDataRepository.updateTurnCount(roomId, room.getTurnCount() + 1);
            roomDataRepository.updateRequiredDraws(roomId, 1);
            messagingTemplate.convertAndSend("/topic/room/" + roomId, "TURN_CHANGED");
        }
    }

    public String drawCard(String roomId, long playerId) {
        if (pendingActions.containsKey(roomId)) {
            return "Wait for action to resolve!";
        }

        long currentTurnPlayer = getCurrentPlayerTurn(roomId);
        if (currentTurnPlayer != playerId) {
            return "Not your turn!";
        }

        String status = playerinRoomRepository.getPlayerStatus(roomId, playerId);
        if (status != null && status.startsWith("PENDING_")) {
            return "You have a pending action!";
        }

        bombRepository.decrementActiveBombs(roomId);

        List<BOMB> bombs = bombRepository.findByRoomId(roomId);
        Optional<BOMB> explodedBomb = bombs.stream()
                .filter(b -> b.getBombCount() == 0)
                .findFirst();

        boolean drawnBomb = false;

        if (explodedBomb.isPresent()) {
            bombRepository.delete(explodedBomb.get().getId());
            drawnBomb = true;
        } else {
            List<DeckListRepository.DeckItem> pool = deckListRepository.getDeckListByRoomId(roomId);
            long unplacedBombs = bombs.stream().filter(b -> b.getBombCount() == -1).count();
            
            int totalCards = pool.stream().mapToInt(DeckListRepository.DeckItem::amount).sum();
            int grandTotal = totalCards + (int)unplacedBombs;
            
            if (grandTotal == 0) return "Deck is empty!";
            
            int roll = new Random().nextInt(grandTotal);
            if (roll < unplacedBombs) {
                Optional<BOMB> randomBomb = bombs.stream().filter(b -> b.getBombCount() == -1).findFirst();
                if(randomBomb.isPresent()) {
                    bombRepository.delete(randomBomb.get().getId());
                }
                drawnBomb = true;
            } else {
                int current = (int)unplacedBombs;
                int drawnCardId = -1;
                for(DeckListRepository.DeckItem item : pool) {
                    current += item.amount();
                    if (roll < current) {
                        drawnCardId = item.cardId();
                        break;
                    }
                }
                if (drawnCardId != -1) {
                    deckListRepository.removeCardFromDeck(roomId, drawnCardId);
                    handCardRepository.addOrUpdateCard(playerId, drawnCardId);
                }
            }
        }

        if (drawnBomb) {
            messagingTemplate.convertAndSend("/topic/room/" + roomId, "PLAYER_DRAWN_BOMB:" + playerId);
            List<HandCard> hand = handCardRepository.findByPlayerId(playerId);
            boolean hasDefuse = hand.stream().anyMatch(h -> h.getCard().getId() == 2 && h.getAmount() > 0);
            
            if (hasDefuse) {
                playerinRoomRepository.updatePlayerStatus(roomId, playerId, "PENDING_DEFUSE");
                return "BOOM! You drew an Exploding Kitten! Please play DEFUSE.";
            } else {
                eliminatePlayer(roomId, playerId);
                return "BOOM! You died!";
            }
        }

        RoomData room = roomDataRepository.findByRoomId(roomId);
        int newDraws = room.getRequiredDraws() - 1;
        
        if (newDraws <= 0) {
            roomDataRepository.updateTurnCount(roomId, room.getTurnCount() + 1);
            roomDataRepository.updateRequiredDraws(roomId, 1);
            messagingTemplate.convertAndSend("/topic/room/" + roomId, "PLAYER_DRAWN_SAFE:" + playerId);
            messagingTemplate.convertAndSend("/topic/room/" + roomId, "TURN_CHANGED");
        } else {
            roomDataRepository.updateRequiredDraws(roomId, newDraws);
            messagingTemplate.convertAndSend("/topic/room/" + roomId, "PLAYER_DRAWN_SAFE_AGAIN:" + playerId);
        }

        return "Safe! You drew a normal card.";
    }

    public String playCards(String roomId, long playerId, List<Integer> cardIds, String cardType, Long targetPlayerId) {
        
        if ("NOPE".equalsIgnoreCase(cardType)) {
            PendingAction pending = pendingActions.get(roomId);
            if (pending == null) {
                return "Nothing to NOPE!";
            }
            
            for (int cardId : cardIds) {
                handCardRepository.removeCardFromHand(playerId, cardId);
            }
            
            pending.nopeCount++;
            messagingTemplate.convertAndSend("/topic/room/" + roomId, "NOPE_PLAYED:" + playerId + ":COUNT:" + pending.nopeCount);
            
            if (pending.timeoutTask != null) {
                pending.timeoutTask.cancel(false);
            }
            
            pending.timeoutTask = scheduler.schedule(() -> resolveAction(roomId), 5, TimeUnit.SECONDS);
            return "NOPE played!";
        }

        long currentTurnPlayer = getCurrentPlayerTurn(roomId);
        if (currentTurnPlayer != playerId) {
            return "Not your turn!"; 
        }

        if (pendingActions.containsKey(roomId)) {
            return "An action is already pending!";
        }

        String status = playerinRoomRepository.getPlayerStatus(roomId, playerId);
        if (status != null && status.startsWith("PENDING_")) {
            if ("PENDING_DEFUSE".equals(status) && !"DEFUSE".equalsIgnoreCase(cardType)) {
                return "You can only play DEFUSE right now!";
            } else if (!"PENDING_DEFUSE".equals(status)) {
                return "You have a pending action!";
            }
        }

        for (int cardId : cardIds) {
            handCardRepository.removeCardFromHand(playerId, cardId);
        }
        
        if ("DEFUSE".equalsIgnoreCase(cardType)) {
            // Defuse doesn't wait for nope
            return "Defuse ready, call defuseBomb API";
        }
        
        messagingTemplate.convertAndSend("/topic/room/" + roomId, "ACTION_PENDING:" + cardType + ":FROM:" + playerId);

        PendingAction action = new PendingAction();
        action.roomId = roomId;
        action.playerId = playerId;
        action.cardType = cardType;
        action.targetPlayerId = targetPlayerId;
        action.nopeCount = 0;
        
        action.timeoutTask = scheduler.schedule(() -> resolveAction(roomId), 5, TimeUnit.SECONDS);
        pendingActions.put(roomId, action);

        return "Action pending (waiting for NOPES)...";
    }

    private void resolveAction(String roomId) {
        PendingAction action = pendingActions.remove(roomId);
        if (action == null) return;
        
        if (action.nopeCount % 2 != 0) {
            messagingTemplate.convertAndSend("/topic/room/" + roomId, "ACTION_CANCELED:" + action.cardType);
            return; 
        }
        
        messagingTemplate.convertAndSend("/topic/room/" + roomId, "ACTION_RESOLVED:" + action.cardType);

        RoomData room = roomDataRepository.findByRoomId(roomId);
        
        if ("NORMAL".equalsIgnoreCase(action.cardType)) {
            if (action.targetPlayerId == null) return;
            List<HandCard> targetHand = handCardRepository.findByPlayerId(action.targetPlayerId);
            List<HandCard> availableCards = targetHand.stream().filter(h -> h.getAmount() > 0).toList();
            if (availableCards.isEmpty()) return;
            
            List<Integer> allTargetCards = new ArrayList<>();
            for (HandCard h : availableCards) {
                for(int i=0; i<h.getAmount(); i++) allTargetCards.add(h.getCard().getId());
            }
            int stolenCardId = allTargetCards.get(new Random().nextInt(allTargetCards.size()));
            
            handCardRepository.removeCardFromHand(action.targetPlayerId, stolenCardId);
            handCardRepository.addOrUpdateCard(action.playerId, stolenCardId);
            
            messagingTemplate.convertAndSend("/topic/room/" + roomId, "PLAYER_STOLE_CARD:" + action.playerId + ":" + action.targetPlayerId);
            return;
        }

        switch (action.cardType.toUpperCase()) {
            case "SKIP":
                int drawsLeft = room.getRequiredDraws() - 1;
                if (drawsLeft <= 0) {
                    roomDataRepository.updateTurnCount(roomId, room.getTurnCount() + 1);
                    roomDataRepository.updateRequiredDraws(roomId, 1);
                    messagingTemplate.convertAndSend("/topic/room/" + roomId, "TURN_CHANGED");
                } else {
                    roomDataRepository.updateRequiredDraws(roomId, drawsLeft);
                }
                break;
                
            case "ATTACK":
                roomDataRepository.updateTurnCount(roomId, room.getTurnCount() + 1);
                roomDataRepository.updateRequiredDraws(roomId, room.getRequiredDraws() + 2); 
                messagingTemplate.convertAndSend("/topic/room/" + roomId, "TURN_CHANGED");
                break;

            case "SHUFFLE":
                bombRepository.resetActiveBombs(roomId);
                List<TOP3> top3Cards = top3Repository.getTop3ByRoomId(roomId);
                for(TOP3 t : top3Cards) {
                   deckListRepository.addCardToDeck(roomId, t.getTop3Count(), 1); 
                }
                top3Repository.deleteByRoomId(roomId);
                break;
                
            case "SEETHEFUTURE":
                List<TOP3> existingTop3 = top3Repository.getTop3ByRoomId(roomId);
                if (existingTop3.isEmpty()) {
                    List<BOMB> bList = bombRepository.findByRoomId(roomId);
                    List<DeckListRepository.DeckItem> pool = deckListRepository.getDeckListByRoomId(roomId);
                    
                    for (int pos = 1; pos <= 3; pos++) {
                        final int currentPos = pos;
                        boolean isBomb = bList.stream().anyMatch(b -> b.getBombCount() == currentPos);
                        
                        TOP3 t = new TOP3();
                        t.setNumber(pos);
                        if (isBomb) {
                            t.setTop3Count(1);
                        } else {
                            int tCount = pool.stream().mapToInt(DeckListRepository.DeckItem::amount).sum();
                            if (tCount > 0) {
                                int r = new Random().nextInt(tCount);
                                int curr = 0;
                                int pickedId = -1;
                                for (DeckListRepository.DeckItem item : pool) {
                                    curr += item.amount();
                                    if (r < curr) {
                                        pickedId = item.cardId();
                                        break;
                                    }
                                }
                                t.setTop3Count(pickedId);
                                deckListRepository.removeCardFromDeck(roomId, pickedId);
                                pool = deckListRepository.getDeckListByRoomId(roomId);
                            } else {
                                break;
                            }
                        }
                        top3Repository.add(roomId, t);
                    }
                }
                break;
                
            case "FAVOR":
                if (action.targetPlayerId == null) return;
                playerinRoomRepository.updatePlayerStatus(roomId, action.targetPlayerId, "PENDING_FAVOR:" + action.playerId);
                messagingTemplate.convertAndSend("/topic/room/" + roomId, "FAVOR_REQUESTED:" + action.targetPlayerId + ":FROM:" + action.playerId);
                break;
        }
    }

    public String giveFavor(String roomId, long playerId, int cardId) {
        String status = playerinRoomRepository.getPlayerStatus(roomId, playerId);
        if (status == null || !status.startsWith("PENDING_FAVOR:")) {
            return "No favor requested from you!";
        }
        
        long requesterId = Long.parseLong(status.split(":")[1]);
        
        handCardRepository.removeCardFromHand(playerId, cardId);
        handCardRepository.addOrUpdateCard(requesterId, cardId);
        
        playerinRoomRepository.updatePlayerStatus(roomId, playerId, "ALIVE");
        messagingTemplate.convertAndSend("/topic/room/" + roomId, "FAVOR_COMPLETED:" + playerId + ":TO:" + requesterId);
        
        return "Favor given successfully!";
    }

    public String defuseBomb(String roomId, long playerId, int putAtPosition) {
        String status = playerinRoomRepository.getPlayerStatus(roomId, playerId);
        if (!"PENDING_DEFUSE".equals(status)) {
            return "You don't have a bomb to defuse!";
        }

        bombRepository.pushBombsDown(roomId, putAtPosition);
        top3Repository.pushTop3Down(roomId, putAtPosition);
        
        BOMB b = new BOMB(roomId, putAtPosition);
        bombRepository.add(b);
        
        playerinRoomRepository.updatePlayerStatus(roomId, playerId, "ALIVE");

        RoomData room = roomDataRepository.findByRoomId(roomId);
        if (room.getRequiredDraws() <= 1) { 
            roomDataRepository.updateTurnCount(roomId, room.getTurnCount() + 1);
            roomDataRepository.updateRequiredDraws(roomId, 1);
            messagingTemplate.convertAndSend("/topic/room/" + roomId, "TURN_CHANGED");
        } else {
            roomDataRepository.updateRequiredDraws(roomId, room.getRequiredDraws() - 1);
        }

        messagingTemplate.convertAndSend("/topic/room/" + roomId, "BOMB_DEFUSED:" + playerId);
        return "Defused successfully!";
    }
}
