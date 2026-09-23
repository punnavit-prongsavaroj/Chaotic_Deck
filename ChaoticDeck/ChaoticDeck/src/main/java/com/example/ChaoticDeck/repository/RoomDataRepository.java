package com.example.ChaoticDeck.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.example.ChaoticDeck.Model.RoomData.RoomData;

@Repository
public class RoomDataRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<RoomData> RoomRowMapper = (rs, rowNum) -> {
        RoomData roomdata = new RoomData();
        roomdata.setId(rs.getInt("id"));
        roomdata.setRoomID(rs.getString("room_id"));
        roomdata.setTop3Count(rs.getInt("top3_count"));
        roomdata.setLedder_id(rs.getInt("leader_id"));
        roomdata.setStatus(rs.getString("status"));
        roomdata.setMaxPlayers(rs.getInt("max_players"));
        return roomdata;
    };

    public RoomDataRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public RoomData findByRoomId(String roomId) {
        String sql = "SELECT * FROM room WHERE room_id = ?";
        return jdbcTemplate.queryForObject(sql, RoomRowMapper, roomId);
    }

    public int add(RoomData roomData) {
        String sql = "INSERT INTO room (room_id, leader_id, top3_count, status, max_players) VALUES (?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql, roomData.getRoomID(), roomData.getLedder_id(), roomData.getTop3Count(), roomData.getStatus(), roomData.getMaxPlayers());
    }

    public boolean existsByRoomId(String roomId) {
        String sql = "SELECT COUNT(*) FROM room WHERE room_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, roomId);
        return count != null && count > 0;
    }

    public int updateTop3Count(String roomId, int top3Count) {
        String sql = "UPDATE room SET top3_count = ? WHERE room_id = ?";
        return jdbcTemplate.update(sql, top3Count, roomId);
    }

    public int delete(String roomId) {
        String sql = "DELETE FROM room WHERE room_id = ?";
        return jdbcTemplate.update(sql, roomId);
    }
}
