package com.example.ChaoticDeck.repository;

import com.example.ChaoticDeck.Model.PlayerinRoom.PlayerinRoom;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PlayerinRoomRepository {

    private final JdbcTemplate jdbcTemplate;

    public PlayerinRoomRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int addPlayerToRoom(String roomId, long playerId) {
        String sql = "INSERT INTO player_in_room (room_id, player_id, status) VALUES (?, ?, 'ALIVE')";
        return jdbcTemplate.update(sql, roomId, playerId);
    }

    public List<Long> getPlayerIdsInRoom(String roomId) {
        String sql = "SELECT player_id FROM player_in_room WHERE room_id = ? ORDER BY id ASC";
        return jdbcTemplate.queryForList(sql, Long.class, roomId);
    }

    public List<Long> getAlivePlayerIdsInRoom(String roomId) {
        String sql = "SELECT player_id FROM player_in_room WHERE room_id = ? AND status = 'ALIVE' ORDER BY id ASC";
        return jdbcTemplate.queryForList(sql, Long.class, roomId);
    }

    public int updatePlayerStatus(String roomId, long playerId, String status) {
        String sql = "UPDATE player_in_room SET status = ? WHERE room_id = ? AND player_id = ?";
        return jdbcTemplate.update(sql, status, roomId, playerId);
    }

    public String getPlayerStatus(String roomId, long playerId) {
        String sql = "SELECT status FROM player_in_room WHERE room_id = ? AND player_id = ?";
        List<String> results = jdbcTemplate.queryForList(sql, String.class, roomId, playerId);
        return results.isEmpty() ? null : results.get(0);
    }

    public int removePlayerFromRoom(String roomId, long playerId) {
        String sql = "DELETE FROM player_in_room WHERE room_id = ? AND player_id = ?";
        return jdbcTemplate.update(sql, roomId, playerId);
    }

    public String findRoomByPlayerId(long playerId) {
        String sql = "SELECT room_id FROM player_in_room WHERE player_id = ? LIMIT 1";
        List<String> results = jdbcTemplate.queryForList(sql, String.class, playerId);
        return results.isEmpty() ? null : results.get(0);
    }

    public int removeAllPlayersFromRoom(String roomId) {
        String sql = "DELETE FROM player_in_room WHERE room_id = ?";
        return jdbcTemplate.update(sql, roomId);
    }

    public boolean isPlayerInRoom(String roomId, long playerId) {
        String sql = "SELECT COUNT(*) FROM player_in_room WHERE room_id = ? AND player_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, roomId, playerId);
        return count != null && count > 0;
    }
}
