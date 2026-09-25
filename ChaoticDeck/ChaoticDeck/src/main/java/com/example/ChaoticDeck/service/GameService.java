package com.example.ChaoticDeck.Service;

import com.example.ChaoticDeck.Model.BOMB.BOMB;
import com.example.ChaoticDeck.Model.RoomData.RoomData;
import com.example.ChaoticDeck.Model.TOP3.TOP3;
import com.example.ChaoticDeck.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class GameService {

    private final BombRepository bombRepository;
    private final DeckListRepository deckListRepository;
    private final HandCardRepository handCardRepository;
    private final RoomDataRepository roomDataRepository;
    private final Top3Repository top3Repository;
    private final PlayerinRoomRepository playerinRoomRepository;

    public GameService(BombRepository bombRepository, 
                       DeckListRepository deckListRepository,
                       HandCardRepository handCardRepository,
                       RoomDataRepository roomDataRepository,
                       Top3Repository top3Repository,
                       PlayerinRoomRepository playerinRoomRepository) {
        this.bombRepository = bombRepository;
        this.deckListRepository = deckListRepository;
        this.handCardRepository = handCardRepository;
        this.roomDataRepository = roomDataRepository;
        this.top3Repository = top3Repository;
        this.playerinRoomRepository = playerinRoomRepository;
    }

    public void startGame(String roomId) {
        List<Long> players = playerinRoomRepository.getPlayerIdsInRoom(roomId);
        int playerCount = players.size();
        
        // แจกไพ่ Defuse ให้ทุกคนคนละ 1 ใบ (สมมติ ID 2 คือ Defuse)
        for (Long playerId : players) {
            handCardRepository.addOrUpdateCard(playerId, 2); 
            // แจกไพ่สุ่มอีก 4 ใบ (จำลอง)
            for (int i=0; i<4; i++) {
                int randomCardId = new Random().nextInt(10) + 3; // สุ่ม ID 3-12
                handCardRepository.addOrUpdateCard(playerId, randomCardId);
            }
        }
        
        // ใส่ระเบิดลงกองกลาง ตามจำนวนผู้เล่น - 1
        int bombCount = playerCount - 1;
        for (int i = 0; i < bombCount; i++) {
            bombRepository.add(new BOMB(roomId, -1));
        }
        
        // ใส่การ์ดปกติลงกองกลาง (จำลองใส่ ID 3 ถึง 12 อย่างละ 4 ใบ)
        for(int cardId=3; cardId<=12; cardId++) {
             deckListRepository.addCardToDeck(roomId, cardId, 4);
        }

        roomDataRepository.updateTurnCount(roomId, 0);
        roomDataRepository.updateRequiredDraws(roomId, 1);
    }

    public String drawCard(String roomId, long playerId) {
        bombRepository.decrementActiveBombs(roomId);

        // เช็คว่ามีระเบิดที่นับถอยหลังถึง 0 ไหม
        List<BOMB> bombs = bombRepository.findByRoomId(roomId);
        Optional<BOMB> explodedBomb = bombs.stream()
                .filter(b -> b.getBombCount() == 0)
                .findFirst();

        if (explodedBomb.isPresent()) {
            bombRepository.delete(explodedBomb.get().getId());
            return "BOOM! You drew an Exploding Kitten! Please play DEFUSE.";
        }

        // สุ่มไพ่จาก DeckList หรือโดนระเบิดจากกองสุ่ม
        List<DeckListRepository.DeckItem> pool = deckListRepository.getDeckListByRoomId(roomId);
        long unplacedBombs = bombs.stream().filter(b -> b.getBombCount() == -1).count();
        
        int totalCards = pool.stream().mapToInt(DeckListRepository.DeckItem::amount).sum();
        int grandTotal = totalCards + (int)unplacedBombs;
        
        if (grandTotal == 0) return "Deck is empty!";
        
        int roll = new Random().nextInt(grandTotal);
        if (roll < unplacedBombs) {
            // จั่วโดนระเบิดสุ่ม! ดึงระเบิด 1 ลูกมากระจาย
            Optional<BOMB> randomBomb = bombs.stream().filter(b -> b.getBombCount() == -1).findFirst();
            if(randomBomb.isPresent()) {
                bombRepository.delete(randomBomb.get().getId());
            }
            return "BOOM! You drew a random Exploding Kitten! Please play DEFUSE.";
        } else {
            // ได้การ์ดปกติ สุ่มจาก pool
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

        // หากรอดตาย ให้ลด required_draws ลง 1
        RoomData room = roomDataRepository.findByRoomId(roomId);
        int newDraws = room.getRequiredDraws() - 1;
        
        if (newDraws <= 0) {
            roomDataRepository.updateTurnCount(roomId, room.getTurnCount() + 1);
            roomDataRepository.updateRequiredDraws(roomId, 1);
        } else {
            roomDataRepository.updateRequiredDraws(roomId, newDraws);
        }

        return "Safe! You drew a normal card.";
    }

    // ฟังก์ชันร่ายการ์ดต่างๆ
    public void playCard(String roomId, long playerId, int cardId, String cardType) {
        RoomData room = roomDataRepository.findByRoomId(roomId);

        // หักการ์ดออกจากมือ
        handCardRepository.removeCardFromHand(playerId, cardId);

        switch (cardType.toUpperCase()) {
            case "SKIP":
                int drawsLeft = room.getRequiredDraws() - 1;
                if (drawsLeft <= 0) {
                    roomDataRepository.updateTurnCount(roomId, room.getTurnCount() + 1);
                    roomDataRepository.updateRequiredDraws(roomId, 1);
                } else {
                    roomDataRepository.updateRequiredDraws(roomId, drawsLeft);
                }
                break;
                
            case "ATTACK":
                roomDataRepository.updateTurnCount(roomId, room.getTurnCount() + 1);
                roomDataRepository.updateRequiredDraws(roomId, room.getRequiredDraws() + 2); 
                break;

            case "SHUFFLE":
                bombRepository.resetActiveBombs(roomId);
                List<TOP3> top3Cards = top3Repository.getTop3ByRoomId(roomId);
                for(TOP3 t : top3Cards) {
                   // top3_count ในบริบทนี้คือ card_id ที่เก็บไว้ (สมมติว่าใช้ช่องนี้เก็บ card_id)
                   deckListRepository.addCardToDeck(roomId, t.getTop3Count(), 1); 
                }
                top3Repository.deleteByRoomId(roomId);
                break;
                
            case "SEETHEFUTURE":
                // ลอจิกสร้าง Top3
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
                            t.setTop3Count(1); // สมมติว่า ID 1 คือ ระเบิด
                        } else {
                            // สุ่มไพ่ 1 ใบจาก DeckList
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
                                // อัปเดต pool (ลด amount ลง 1 ในหน่วยความจำเพื่อให้การสุ่มใบต่อไปถูกต้อง)
                                pool = deckListRepository.getDeckListByRoomId(roomId);
                            } else {
                                break; // กองไพ่หมดแล้ว
                            }
                        }
                        top3Repository.add(roomId, t);
                    }
                }
                break;
        }
    }

    public void defuseBomb(String roomId, int putAtPosition) {
        // ดันระเบิดที่อยู่คิวหลังๆ ลงไป 1 สเต็ป
        bombRepository.pushBombsDown(roomId, putAtPosition);
        // ดันไพ่ใน TOP3 ลงไป 1 สเต็ปด้วย
        top3Repository.pushTop3Down(roomId, putAtPosition);
        
        // ใส่ลูกใหม่เข้าไป
        BOMB b = new BOMB(roomId, putAtPosition);
        bombRepository.add(b);
        
        // Defuse จบถือว่าจบเทิร์น (ถ้าไม่มี required_draws ค้าง)
        RoomData room = roomDataRepository.findByRoomId(roomId);
        if (room.getRequiredDraws() <= 1) { // 1 คือดึงไพ่ใบนั้นไปแล้วเหลือ 0
            roomDataRepository.updateTurnCount(roomId, room.getTurnCount() + 1);
            roomDataRepository.updateRequiredDraws(roomId, 1);
        }
    }

    public long getCurrentPlayerTurn(String roomId, List<Long> allPlayerIdsInRoom, int currentTurnCount) {
        if (allPlayerIdsInRoom == null || allPlayerIdsInRoom.isEmpty()) {
            throw new RuntimeException("No players in room");
        }
        int playerCount = allPlayerIdsInRoom.size();
        int turnIndex = currentTurnCount % playerCount;
        return allPlayerIdsInRoom.get(turnIndex);
    }
}
