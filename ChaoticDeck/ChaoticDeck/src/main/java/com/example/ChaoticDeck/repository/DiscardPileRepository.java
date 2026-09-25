package com.example.ChaoticDeck.repository;

import com.example.ChaoticDeck.Model.DiscardPile.DiscardPile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DiscardPileRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<DiscardPile> discardRowMapper = (rs, rowNum) -> {
        DiscardPile d = new DiscardPile();
        d.setId(rs.getInt("id"));
        d.setRoomId(rs.getString("room_id"));
        d.setCardId(rs.getInt("card_id"));
        d.setPlayedBy(rs.getLong("played_by"));
        d.setPlayedAt(rs.getTimestamp("played_at"));
        return d;
    };

    public DiscardPileRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<DiscardPile> getDiscardPileByRoomId(String roomId) {
        String sql = "SELECT * FROM discard_pile WHERE room_id = ? ORDER BY played_at DESC";
        return jdbcTemplate.query(sql, discardRowMapper, roomId);
    }

    public void add(String roomId, int cardId, long playedBy) {
        String sql = "INSERT INTO discard_pile (room_id, card_id, played_by) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, roomId, cardId, playedBy);
    }

    public void remove(int id) {
        String sql = "DELETE FROM discard_pile WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
    
    public void deleteByRoomId(String roomId) {
        String sql = "DELETE FROM discard_pile WHERE room_id = ?";
        jdbcTemplate.update(sql, roomId);
    }
}
