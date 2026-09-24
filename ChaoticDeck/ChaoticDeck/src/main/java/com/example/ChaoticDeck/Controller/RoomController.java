package com.example.ChaoticDeck.controller;


import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.ChaoticDeck.Model.RoomData.RoomData;
import com.example.ChaoticDeck.service.RoomService;

@RestController
@RequestMapping("/Room")
public class RoomController {
    
    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

   @PostMapping
    public RoomData createRoom(@RequestParam String roomId, @RequestParam int leaderId) {
        return roomService.createRoom(roomId, leaderId);
    }
 
    // เข้าห้อง (ส่ง playerId มาด้วยตรงๆ แบบง่าย ยังไม่มีระบบ session)
    @PostMapping("/{roomId}/join")
    public void joinRoom(@PathVariable String roomId, @RequestParam long playerId) {
        roomService.joinRoom(roomId, playerId);
    }
 
    // ดูข้อมูลห้อง
    @GetMapping("/{roomId}")
    public List<Long> getRoom(@PathVariable String roomId) {
        return roomService.getPlayersInRoom(roomId);
    }

}
