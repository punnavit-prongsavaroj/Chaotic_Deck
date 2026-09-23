package com.example.ChaoticDeck.service;

import com.example.ChaoticDeck.Model.BOMB.BOMB;
import com.example.ChaoticDeck.Model.RoomData.RoomData;
import com.example.ChaoticDeck.repository.BombRepository;
import com.example.ChaoticDeck.repository.DeckListRepository;
import com.example.ChaoticDeck.repository.HandCardRepository;
import com.example.ChaoticDeck.repository.RoomDataRepository;
import com.example.ChaoticDeck.repository.Top3Repository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GameService {

    private final BombRepository bombRepository;
    private final DeckListRepository deckListRepository;
    private final HandCardRepository handCardRepository;
    private final RoomDataRepository roomDataRepository;
    private final Top3Repository top3Repository;

    public GameService(BombRepository bombRepository, 
                       DeckListRepository deckListRepository,
                       HandCardRepository handCardRepository,
                       RoomDataRepository roomDataRepository,
                       Top3Repository top3Repository) {
        this.bombRepository = bombRepository;
        this.deckListRepository = deckListRepository;
        this.handCardRepository = handCardRepository;
        this.roomDataRepository = roomDataRepository;
        this.top3Repository = top3Repository;
    }

    public void startGame(String roomId, int playerCount) {
        int bombCount = playerCount - 1;
        for (int i = 0; i < bombCount; i++) {
            bombRepository.add(new BOMB(roomId, -1));
        }
        roomDataRepository.updateTurnCount(roomId, 0);
        roomDataRepository.updateRequiredDraws(roomId, 1);
    }

    public String drawCard(String roomId, long playerId) {
        bombRepository.decrementActiveBombs(roomId);

        List<BOMB> bombs = bombRepository.findByRoomId(roomId);
        Optional<BOMB> explodedBomb = bombs.stream()
                .filter(b -> b.getBombCount() == 0)
                .findFirst();

        if (explodedBomb.isPresent()) {
            bombRepository.delete(explodedBomb.get().getId());
            return "BOOM! You drew an Exploding Kitten!";
        }

        // หากรอดตาย ให้ลด required_draws ลง 1
        RoomData room = roomDataRepository.findByRoomId(roomId);
        int newDraws = room.getRequiredDraws() - 1;
        
        if (newDraws <= 0) {
            // จบเทิร์นสมบูรณ์ สลับไปคนถัดไป
            roomDataRepository.updateTurnCount(roomId, room.getTurnCount() + 1);
            roomDataRepository.updateRequiredDraws(roomId, 1);
        } else {
            // โดน Attack มา ยังเหลือที่ต้องจั่วอีก
            roomDataRepository.updateRequiredDraws(roomId, newDraws);
        }

        return "Safe! You drew a normal card.";
    }

    // ฟังก์ชันร่ายการ์ดต่างๆ
    public void playCard(String roomId, long playerId, String cardType) {
        RoomData room = roomDataRepository.findByRoomId(roomId);

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
                // จบเทิร์นคนนี้ โยน 2 เทิร์นให้คนถัดไป
                roomDataRepository.updateTurnCount(roomId, room.getTurnCount() + 1);
                // ถ้าอยากให้โจมตีซ้อนทบกันได้ ก็ใช้ room.getRequiredDraws() + 2
                roomDataRepository.updateRequiredDraws(roomId, 2); 
                break;

            case "SHUFFLE":
                bombRepository.resetActiveBombs(roomId);
                // TODO: ดึง top3 กลับเข้า decklist ก่อนลบ
                top3Repository.deleteByRoomId(roomId);
                break;
                
            case "SEETHEFUTURE":
                // ดึง top3 ปกติ, หรือถ้าไม่มีก็สร้างใหม่ (มีเช็คระเบิด)
                break;
        }
        // TODO: ลบไพ่ออกจากมือ (hand_card)
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
