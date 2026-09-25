package com.example.ChaoticDeck.controller;

import com.example.ChaoticDeck.Model.RoomData.RoomData;
import com.example.ChaoticDeck.service.RoomService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    // สร้างห้องใหม่
    // POST http://localhost:8080/api/rooms?roomId=ROOM123&leaderId=1
    @PostMapping
    public RoomData createRoom(@RequestParam String roomId, @RequestParam int leaderId) {
        return roomService.createRoom(roomId, leaderId);
    }

    // เข้าร่วมห้อง
    // POST http://localhost:8080/api/rooms/ROOM123/join?playerId=2
    @PostMapping("/{roomId}/join")
    public String joinRoom(@PathVariable String roomId, @RequestParam long playerId) {
        try {
            roomService.joinRoom(roomId, playerId);
            return "Player " + playerId + " joined room " + roomId + " successfully!";
        } catch (Exception e) {
            return "Failed to join room: " + e.getMessage();
        }
    }

    // ดูรายชื่อผู้เล่นในห้อง
    // GET http://localhost:8080/api/rooms/ROOM123/players
    @GetMapping("/{roomId}/players")
    public List<Long> getPlayersInRoom(@PathVariable String roomId) {
        return roomService.getPlayersInRoom(roomId);
    }

    // ปิด/ลบห้อง
    // DELETE http://localhost:8080/api/rooms/ROOM123
    @DeleteMapping("/{roomId}")
    public String closeRoom(@PathVariable String roomId) {
        roomService.closeRoom(roomId);
        return "Room " + roomId + " closed.";
    }
}
