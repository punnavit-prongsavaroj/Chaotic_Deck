package com.example.ChaoticDeck.Controller;

import com.example.ChaoticDeck.Model.Player.Player;
import com.example.ChaoticDeck.Service.GameService;
import com.example.ChaoticDeck.Service.PlayerService;
import com.example.ChaoticDeck.Service.RoomService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Player")
@CrossOrigin(origins = "*")
public class PlayerController {

    private final PlayerService playerService;
    private final RoomService roomService;
 
    public PlayerController(PlayerService playerService, RoomService roomService) {
        this.playerService = playerService;
        this.roomService = roomService;
    }
 
    // สร้างผู้เล่นใหม่ (กรอกชื่อตอนเปิดแอป)
    @PostMapping
    public Player createPlayer(@RequestBody Player request) {
        Player player = new Player();
        player.setName(request.getName());   // set เฉพาะ field ที่อนุญาต
        return playerService.createPlayer(player);
    }
 
    // ดูข้อมูลผู้เล่น
    @GetMapping("/{id}")
    public Player getPlayer(@PathVariable Long id) {
        return playerService.getPlayerById(id);
    }

    // ตรวจสอบว่า Player ID นี้ยังมีอยู่ใน DB หรือไม่ (สำหรับ Cookie validation)
    @GetMapping("/{id}/exists")
    public boolean exists(@PathVariable Long id) {
        return playerService.existsById(id);
    }

    // ตรวจสอบว่าผู้เล่นคนนี้กำลังอยู่ในห้องไหนหรือไม่ (สำหรับ Auto-rejoin)
    @GetMapping("/{id}/room")
    public ResponseEntity<?> getPlayerRoom(@PathVariable Long id) {
        String roomId = roomService.getRoomByPlayerId(id);
        if (roomId == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(java.util.Collections.singletonMap("roomId", roomId));
    }

}