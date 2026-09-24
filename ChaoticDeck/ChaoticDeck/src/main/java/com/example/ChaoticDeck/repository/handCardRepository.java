package com.example.ChaoticDeck.repository;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.example.ChaoticDeck.Model.HandCard.HandCard;
import com.example.ChaoticDeck.Model.Player.Player;
import com.example.ChaoticDeck.Model.Card.Card;

@Repository
public class HandCardRepository {
    private final JdbcTemplate jdbcTemplate;

    // RowMapper สำหรับ HandCard (ดึง player_id และ card_id มาประกอบกัน)
    private final RowMapper<HandCard> handCardRowMapper = (rs, rowNum) -> {
        HandCard handCard = new HandCard();
        handCard.setId(rs.getInt("id"));
        
        Player p = new Player();
        p.setId(rs.getLong("player_id"));
        handCard.setPlayer(p);
        
        int cardId = rs.getInt("card_id");
        Card c = new Card() {};
        c.setId(cardId);
        handCard.setCard(c);
        
        handCard.setAmount(rs.getInt("amount"));

        return handCard;
    };

    public HandCardRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<HandCard> findByPlayerId(long playerId) {
        String sql = "SELECT * FROM hand_card WHERE player_id = ?";
        return jdbcTemplate.query(sql, handCardRowMapper, playerId);
    }

    public HandCard findById(int id) {
        String sql = "SELECT * FROM hand_card WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, handCardRowMapper, id);
    }

    public int add(HandCard handCard) {
        String sql = "INSERT INTO hand_card (player_id, card_id, amount) VALUES (?, ?, ?)";
        return jdbcTemplate.update(sql, 
            handCard.getPlayer().getId(), 
            handCard.getCard().getId(), 
            handCard.getAmount()
        );
    }

    public int updateAmount(int id, int amount) {
        String sql = "UPDATE hand_card SET amount = ? WHERE id = ?";
        return jdbcTemplate.update(sql, amount, id);
    }

    public int removeCardFromHand(long playerId, int cardId) {
        // หักจำนวนการ์ดลง 1 ใบ
        String sql = "UPDATE hand_card SET amount = amount - 1 WHERE player_id = ? AND card_id = ? AND amount > 0";
        return jdbcTemplate.update(sql, playerId, cardId);
    }
    
    public int addOrUpdateCard(long playerId, int cardId) {
        // เพิ่มไพ่เข้ามือ ถ้ามีอยู่แล้วให้บวก amount
        String sqlCheck = "SELECT count(*) FROM hand_card WHERE player_id = ? AND card_id = ?";
        int count = jdbcTemplate.queryForObject(sqlCheck, Integer.class, playerId, cardId);
        if (count > 0) {
            String sqlUpdate = "UPDATE hand_card SET amount = amount + 1 WHERE player_id = ? AND card_id = ?";
            return jdbcTemplate.update(sqlUpdate, playerId, cardId);
        } else {
            String sqlInsert = "INSERT INTO hand_card (player_id, card_id, amount) VALUES (?, ?, 1)";
            return jdbcTemplate.update(sqlInsert, playerId, cardId);
        }
    }

    public int delete(int id) {
        String sql = "DELETE FROM hand_card WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }
}
