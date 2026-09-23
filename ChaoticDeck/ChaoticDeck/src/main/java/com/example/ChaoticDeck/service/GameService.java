package com.example.ChaoticDeck.Service;

import com.example.ChaoticDeck.Model.BOMB.BOMB;
import com.example.ChaoticDeck.repository.BombRepository;
import com.example.ChaoticDeck.repository.DeckListRepository;
import com.example.ChaoticDeck.repository.HandCardRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GameService {

    private final BombRepository bombRepository;
    private final DeckListRepository deckListRepository;
    private final HandCardRepository handCardRepository;

    public GameService(BombRepository bombRepository, 
                       DeckListRepository deckListRepository,
                       HandCardRepository handCardRepository) {
        this.bombRepository = bombRepository;
        this.deckListRepository = deckListRepository;
        this.handCardRepository = handCardRepository;
    }

    // ฟังก์ชันจำลองการเริ่มเกม
    public void startGame(String roomId, int playerCount) {
        // 1. แจกไพ่ปกติให้ผู้เล่น (จำลอง)
        
        // 2. เอาระเบิดใส่กอง ตามจำนวนผู้เล่น - 1
        int bombCount = playerCount - 1;
        for (int i = 0; i < bombCount; i++) {
            // ตั้งค่าเริ่มต้นเป็น -1 (อยู่ในกองสุ่ม)
            bombRepository.add(new BOMB(roomId, -1));
        }
    }

    // ฟังก์ชันจั่วไพ่ของเบสิค
    public String drawCard(String roomId, long playerId) {
        // 1. ลดค่า bomb_count ของระเบิดที่กำลังนับถอยหลังอยู่ (>0)
        bombRepository.decrementActiveBombs(roomId);

        // 2. เช็คว่ามีระเบิดลูกไหนที่ระเบิดตูมไหม (bomb_count = 0)
        List<BOMB> bombs = bombRepository.findByRoomId(roomId);
        Optional<BOMB> explodedBomb = bombs.stream()
                .filter(b -> b.getBombCount() == 0)
                .findFirst();

        if (explodedBomb.isPresent()) {
            // โดนระเบิด! ต้องลบระเบิดลูกนี้ทิ้ง หรือให้โอกาส Defuse
            bombRepository.delete(explodedBomb.get().getId());
            return "BOOM! You drew an Exploding Kitten!";
        }

        // 3. ถ้าไม่มีระเบิดที่นับถอยหลังถึง 0 ให้สุ่มไพ่จาก DeckList ปกติ
        // สมมติว่าได้การ์ด id = 5
        // handCardRepository.add(new HandCard(...))
        return "Safe! You drew a normal card.";
    }

    // ฟังก์ชันเมื่อผู้เล่นใช้การ์ด Defuse แล้วเลือกว่าจะซ่อนที่ไหน (เช่น ใบที่ 3)
    public void defuseBomb(String roomId, int putAtPosition) {
        // สร้างระเบิดลูกใหม่ (แทนลูกที่เพิ่งลบไปตอนระเบิด) หรืออัปเดตลูกเดิม
        BOMB b = new BOMB(roomId, putAtPosition);
        bombRepository.add(b);
    }
}
