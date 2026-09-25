package com.example.ChaoticDeck.Controller;


import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.ChaoticDeck.Model.RoomData.RoomData;
import com.example.ChaoticDeck.Service.RoomService;

@RestController
@RequestMapping("/Room")
@CrossOrigin(origins = "*")
public class RoomController {
    
    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    // สร้างห้องใหม่ (Auto-generate Room ID, Leader ตั้งจำนวนผู้เล่นได้ 2-8 คน)
    @PostMapping("/create")
    public RoomData createRoomAuto(@RequestParam long playerId, @RequestParam(defaultValue = "4") int maxPlayers) {
        return roomService.createRoom(playerId, maxPlayers);
    }

    // สร้างห้องแบบระบุ Room ID เอง (backward-compatible)
    // @PostMapping
    // public RoomData createRoom(@RequestParam(required = false) String roomId, 
    //                           @RequestParam(required = false) Integer leaderId,
    //                           @RequestParam(required = false) Long playerId,
    //                           @RequestParam(defaultValue = "4") int maxPlayers) {
    //     long effectiveLeader = playerId != null ? playerId : (leaderId != null ? leaderId : 0L);
    //     return roomService.createRoom(effectiveLeader, maxPlayers);
    // }
 
    // เข้าร่วมห้อง
    @PostMapping("/{roomId}/join")
    public ResponseEntity<String> joinRoom(@PathVariable String roomId, @RequestParam long playerId) {
        roomService.joinRoom(roomId, playerId);
        return ResponseEntity.ok("Joined room " + roomId);
    }

    // ออกจากห้อง (ถ้า Leader ออก จะยุบห้องทั้งห้อง)
    @PostMapping("/{roomId}/leave")
    public ResponseEntity<String> leaveRoom(@PathVariable String roomId, @RequestParam long playerId) {
        roomService.leaveRoom(roomId, playerId);
        return ResponseEntity.ok("Left room " + roomId);
    }
 
    // ดูข้อมูลห้องแบบละเอียด พร้อมรายชื่อผู้เล่นและชื่อ (สำหรับ Lobby/Waiting Room)
    @GetMapping("/{roomId}")
    public Map<String, Object> getRoom(@PathVariable String roomId) {
        return roomService.getRoomDetails(roomId);
    }

    // ดูเฉพาะ ID ผู้เล่นในห้อง
    @GetMapping("/{roomId}/players")
    public List<Long> getRoomPlayers(@PathVariable String roomId) {
        return roomService.getPlayersInRoom(roomId);
    }

}
