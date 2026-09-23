package com.example.ChaoticDeck.repository;

import com.example.ChaoticDeck.Model.Card.Card;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CardRepository {

    private final JdbcTemplate jdbcTemplate;

    // Assuming Card is a concrete representation from DB when fetching
    private final RowMapper<Card> cardRowMapper = (rs, rowNum) -> {
        // Since Card is abstract, we could instantiate a concrete class based on Type
        // For simplicity in JDBC mapping, we create an anonymous subclass or concrete subclass
        return new Card(
                rs.getInt("id"),
                rs.getString("type"),
                rs.getString("name")
        ) {
            // Anonymous subclass since Card is abstract
        };
    };

    public CardRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Card> findAll() {
        String sql = "SELECT * FROM card";
        return jdbcTemplate.query(sql, cardRowMapper);
    }

    public Card findById(int id) {
        String sql = "SELECT * FROM card WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, cardRowMapper, id);
    }
}
