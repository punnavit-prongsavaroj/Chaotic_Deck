package com.example.ChaoticDeck.repository;


import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.example.ChaoticDeck.Model.Player.Player;
import com.example.ChaoticDeck.Model.RoomData.RoomData;

import java.util.List;
public class roomRepository {

    private final JdbcTemplate jdbcTemplate;


    private final RowMapper<RoomData> RoomRowMapper = (rs, rowNum) -> {
        RoomData roomdata = new RoomData();
        roomdata.setRoomID(rs.getString("RoomID"));
        roomdata.setTop3Count(rs.getInt("top3_count"));
        roomdata.setLeaderId(rs.getInt("leader_id"));
        roomdata.setPlayerCount(rs.getInt("player_count"));
        roomdata.setMaxPlayers(rs.getInt("max_players"));

        return roomdata;
};

    public roomRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }



    public RoomData findById(String id) {
        String sql = "SELECT * FROM room WHERE RoomID = ?";
        return jdbcTemplate.queryForObject(sql, RoomRowMapper, id);
    }

    public int add(RoomData roomData) {
        String sql = "INSERT INTO RoomData (name) VALUES (?)";
        return jdbcTemplate.update(sql, roomData.getRoomID());
    }

    public int delte(RoomData roomData) {
        String sql = "DELETE FROM room WHERE RoomID = ?";
        return jdbcTemplate.update(sql, roomData.getRoomID());
    }


    
}
