package com.example.ChaoticDeck.repository;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import com.example.ChaoticDeck.Model.HandCard.HandCard;

import com.example.ChaoticDeck.Model.Player.Player;

@Repository 
public class handCardRepository {
     private final JdbcTemplate jdbcTemplate;

    // RowMapper แปลงแต่ละแถวของ ResultSet เป็น Player object
    private final RowMapper<HandCard> playerRowMapper = (rs, rowNum) -> {
        HandCard handCard = new HandCard();

        handCard.setId(rs.getInt("id"));
        handCard.setAmount(rs.getInt("amount"));

        return handCard;
    };

    public handCardRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Player> pullHandData() {
        String sql = "SELECT * FROM player";
        return jdbcTemplate.query(sql, playerRowMapper);
    }

    public Player findById(Long id) {
        String sql = "SELECT * FROM player WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, playerRowMapper, id);
    }

    public int add(Player player) {
        String sql = "INSERT INTO player (name) VALUES (?)";
        return jdbcTemplate.update(sql, player.getName());
    }

    public int delte(Player player) {
        String sql = "DELETE FROM player WHERE name = ?";
        return jdbcTemplate.update(sql, player.getName());
    }
}
