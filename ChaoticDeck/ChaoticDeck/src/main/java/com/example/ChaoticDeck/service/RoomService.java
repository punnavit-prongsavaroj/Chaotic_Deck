package com.example.ChaoticDeck.Service;

import com.example.ChaoticDeck.Model.Player.Player;
import com.example.ChaoticDeck.Model.RoomData.RoomData;
import com.example.ChaoticDeck.repository.PlayerRepository;
import com.example.ChaoticDeck.repository.PlayerinRoomRepository;
import com.example.ChaoticDeck.repository.RoomDataRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.util.*;

@Service
public class RoomService {

    private final RoomDataRepository roomDataRepository;
    private final PlayerinRoomRepository playerinRoomRepository;
    private final PlayerRepository playerRepository;

    private static final String ROOM_ID_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private final SecureRandom random = new SecureRandom();

    public RoomService(RoomDataRepository roomDataRepository, 
                       PlayerinRoomRepository playerinRoomRepository,
                       PlayerRepository playerRepository) {
        this.roomDataRepository = roomDataRepository;
        this.playerinRoomRepository = playerinRoomRepository;
        this.playerRepository = playerRepository;
    }

    // สุ่มสร้างรหัสห้อง 6 ตัวอักษรที่ไม่ซ้ำ
    public String generateRoomId() {
        StringBuilder sb = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            sb.append(ROOM_ID_CHARS.charAt(random.nextInt(ROOM_ID_CHARS.length())));
        }
        String id = sb.toString();
        if (roomDataRepository.existsByRoomId(id)) {
            return generateRoomId();
        }
        return id;
    }

    // สร้างห้องใหม่ (Leader ตั้งจำนวนผู้เล่นได้ 2-8 คน)
    public RoomData createRoom(long leaderId, int maxPlayers) {
        // หากผู้เล่นเคยอยู่ในห้องอื่น ให้เอาออกจากห้องเดิมก่อน
        String oldRoom = playerinRoomRepository.findRoomByPlayerId(leaderId);
        if (oldRoom != null) {
            leaveRoom(oldRoom, leaderId);
        }

        String roomId = generateRoomId();
        int safeMax = (maxPlayers >= 2 && maxPlayers <= 8) ? maxPlayers : 4;

        RoomData room = new RoomData();
        room.setRoomID(roomId);
        room.setLedder_id((int) leaderId);
        room.setTop3Count(0);
        room.setStatus("WAITING");
        room.setMaxPlayers(safeMax);

        roomDataRepository.add(room);
        playerinRoomRepository.addPlayerToRoom(roomId, leaderId);

        return room;
    }

    // เข้าห้อง พร้อมตรวจสอบเงื่อนไข (ห้องมีไหม / เริ่มหรือยัง / ห้องเต็มไหม)
    public void joinRoom(String roomId, long playerId) {
        roomId = roomId.trim().toUpperCase();

        if (!roomDataRepository.existsByRoomId(roomId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ไม่พบรหัสห้องนี้ในระบบ");
        }

        RoomData room = roomDataRepository.getRoomData(roomId);
        if (!"WAITING".equalsIgnoreCase(room.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ห้องนี้เริ่มเล่นเกมไปแล้ว ไม่สามารถเข้าร่วมได้");
        }

        // ถ้าอยู่ในห้องนี้อยู่แล้ว อนุญาตให้เข้าได้ (Rejoin)
        if (playerinRoomRepository.isPlayerInRoom(roomId, playerId)) {
            return;
        }

        // ตรวจสอบจำนวนผู้เล่น
        List<Long> playerIds = playerinRoomRepository.getPlayerIdsInRoom(roomId);
        int max = room.getMaxPlayers() > 0 ? room.getMaxPlayers() : 4;
        if (playerIds.size() >= max) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ห้องนี้เต็มแล้ว (" + max + "/" + max + " คน)");
        }

        // หากผู้เล่นเคยอยู่ในห้องอื่น ให้เคลียร์ออกจากห้องเดิมก่อน
        String oldRoom = playerinRoomRepository.findRoomByPlayerId(playerId);
        if (oldRoom != null && !oldRoom.equals(roomId)) {
            leaveRoom(oldRoom, playerId);
        }

        playerinRoomRepository.addPlayerToRoom(roomId, playerId);
    }

    // ออกจากห้อง (ถ้า Leader ออก -> ยุบห้อง kick ทุกคน)
    public void leaveRoom(String roomId, long playerId) {
        if (!roomDataRepository.existsByRoomId(roomId)) {
            playerinRoomRepository.removePlayerFromRoom(roomId, playerId);
            return;
        }

        RoomData room = roomDataRepository.getRoomData(roomId);
        if (room.getLedder_id() == (int) playerId) {
            // Leader ออก -> ยุบห้อง
            playerinRoomRepository.removeAllPlayersFromRoom(roomId);
            roomDataRepository.delete(roomId);
        } else {
            // ผู้เล่นทั่วไปออก
            playerinRoomRepository.removePlayerFromRoom(roomId, playerId);
            List<Long> remaining = playerinRoomRepository.getPlayerIdsInRoom(roomId);
            if (remaining.isEmpty()) {
                roomDataRepository.delete(roomId);
            }
        }
    }

    // หาห้องที่ผู้เล่นกำลังอยู่ (สำหรับ Auto-rejoin)
    public String getRoomByPlayerId(long playerId) {
        String roomId = playerinRoomRepository.findRoomByPlayerId(playerId);
        if (roomId != null) {
            if (roomDataRepository.existsByRoomId(roomId)) {
                return roomId;
            } else {
                // ข้อมูลตกค้าง ให้ลบออก
                playerinRoomRepository.removePlayerFromRoom(roomId, playerId);
            }
        }
        return null;
    }

    // ดูรายชื่อผู้เล่นทั้งหมดในห้อง (เฉพาะ ID)
    public List<Long> getPlayersInRoom(String roomId) {
        return playerinRoomRepository.getPlayerIdsInRoom(roomId);
    }

    // ดูข้อมูลห้องแบบละเอียด พร้อมชื่อผู้เล่น (สำหรับ UI ห้องรอ)
    public Map<String, Object> getRoomDetails(String roomId) {
        if (!roomDataRepository.existsByRoomId(roomId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ไม่พบห้องนี้");
        }

        RoomData room = roomDataRepository.getRoomData(roomId);
        List<Long> playerIds = playerinRoomRepository.getPlayerIdsInRoom(roomId);

        List<Map<String, Object>> players = new ArrayList<>();
        for (Long pid : playerIds) {
            Map<String, Object> pInfo = new HashMap<>();
            pInfo.put("id", pid);
            try {
                Player p = playerRepository.findById(pid);
                pInfo.put("name", p != null ? p.getName() : "Player " + pid);
            } catch (Exception e) {
                pInfo.put("name", "Player " + pid);
            }
            pInfo.put("isLeader", pid == room.getLedder_id());
            players.add(pInfo);
        }

        Map<String, Object> details = new HashMap<>();
        details.put("roomId", room.getRoomID());
        details.put("leaderId", room.getLedder_id());
        details.put("status", room.getStatus() != null ? room.getStatus() : "WAITING");
        details.put("maxPlayers", room.getMaxPlayers() > 0 ? room.getMaxPlayers() : 4);
        details.put("playerCount", playerIds.size());
        details.put("players", players);

        return details;
    }

    // ปิดห้อง/ลบห้อง
    public void closeRoom(String roomId) {
        playerinRoomRepository.removeAllPlayersFromRoom(roomId);
        roomDataRepository.delete(roomId);
    }

    public RoomData getRoomData(String roomId) {
        return roomDataRepository.getRoomData(roomId);
    }
}
