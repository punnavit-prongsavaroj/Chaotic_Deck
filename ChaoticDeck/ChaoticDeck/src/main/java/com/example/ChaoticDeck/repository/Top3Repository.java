package com.example.ChaoticDeck.repository;

import com.example.ChaoticDeck.Model.TOP3.TOP3;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class Top3Repository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<TOP3> top3RowMapper = (rs, rowNum) -> {
        TOP3 top3 = new TOP3();
        top3.setId(rs.getInt("id"));
        top3.setNumber(rs.getInt("number"));
        top3.setTop3Count(rs.getInt("top3_count"));
        return top3;
    };

    public Top3Repository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<TOP3> findByRoomId(String roomId) {
        String sql = "SELECT * FROM top3 WHERE room_id = ?";
        return jdbcTemplate.query(sql, top3RowMapper, roomId);
    }

    public int add(String roomId, TOP3 top3) {
        String sql = "INSERT INTO top3 (room_id, number, top3_count) VALUES (?, ?, ?)";
        return jdbcTemplate.update(sql, roomId, top3.getNumber(), top3.getTop3Count());
    }

    public int delete(int id) {
        String sql = "DELETE FROM top3 WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }
}
