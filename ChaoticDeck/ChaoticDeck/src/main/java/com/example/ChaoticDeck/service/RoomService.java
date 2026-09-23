package com.example.ChaoticDeck.service;

import com.example.ChaoticDeck.Model.RoomData.RoomData;
import com.example.ChaoticDeck.repository.PlayerinRoomRepository;
import com.example.ChaoticDeck.repository.RoomDataRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomService {

    private final RoomDataRepository roomDataRepository;
    private final PlayerinRoomRepository playerinRoomRepository;

    public RoomService(RoomDataRepository roomDataRepository, PlayerinRoomRepository playerinRoomRepository) {
        this.roomDataRepository = roomDataRepository;
        this.playerinRoomRepository = playerinRoomRepository;
    }

    // สร้างห้องใหม่
    public RoomData createRoom(String roomId, int leaderId) {
        RoomData room = new RoomData();
        room.setRoomID(roomId);
        room.setLedder_id(leaderId);
        room.setTop3Count(0);
        
        roomDataRepository.add(room);
        
        // เอา Leader เข้าห้องอัตโนมัติ
        playerinRoomRepository.addPlayerToRoom(roomId, leaderId);
        
        return room;
    }

    // ผู้เล่นคนอื่นจอยเข้าห้อง
    public void joinRoom(String roomId, long playerId) {
        List<Long> currentPlayers = playerinRoomRepository.getPlayerIdsInRoom(roomId);
        if (currentPlayers.size() >= 5) {
            throw new RuntimeException("Room is full! Maximum 5 players allowed.");
        }
        if (currentPlayers.contains(playerId)) {
            throw new RuntimeException("Player is already in the room.");
        }
        playerinRoomRepository.addPlayerToRoom(roomId, playerId);
    }

    // ดูว่าในห้องมีใครบ้าง
    public List<Long> getPlayersInRoom(String roomId) {
        return playerinRoomRepository.getPlayerIdsInRoom(roomId);
    }

    // ปิดห้อง/ลบห้อง
    public void closeRoom(String roomId) {
        roomDataRepository.delete(roomId);
    }
}
