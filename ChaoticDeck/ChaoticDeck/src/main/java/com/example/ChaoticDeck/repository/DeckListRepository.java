package com.example.ChaoticDeck.repository;

import com.example.ChaoticDeck.Model.DeckList.DeckList;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class DeckListRepository {

    private final JdbcTemplate jdbcTemplate;

    public DeckListRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // A decklist usually maps cards to a room with counts
    // For simplicity, we just add records for the room_id and card_id
    public int addCardToDeck(String roomId, int cardId, int amount) {
        String sql = "INSERT INTO decklist (room_id, card_id, amount) VALUES (?, ?, ?)";
        return jdbcTemplate.update(sql, roomId, cardId, amount);
    }
    
    public int updateCardAmount(String roomId, int cardId, int newAmount) {
        String sql = "UPDATE decklist SET amount = ? WHERE room_id = ? AND card_id = ?";
        return jdbcTemplate.update(sql, newAmount, roomId, cardId);
    }

    public int deleteByRoomId(String roomId) {
        String sql = "DELETE FROM decklist WHERE room_id = ?";
        return jdbcTemplate.update(sql, roomId);
    }
}
