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
        String sql = "INSERT INTO player_in_room (room_id, player_id) VALUES (?, ?)";
        return jdbcTemplate.update(sql, roomId, playerId);
    }

    public List<Long> getPlayerIdsInRoom(String roomId) {
        String sql = "SELECT player_id FROM player_in_room WHERE room_id = ?";
        return jdbcTemplate.queryForList(sql, Long.class, roomId);
    }

    public int removePlayerFromRoom(String roomId, long playerId) {
        String sql = "DELETE FROM player_in_room WHERE room_id = ? AND player_id = ?";
        return jdbcTemplate.update(sql, roomId, playerId);
    }
}
