package com.example.ChaoticDeck.repository;

import com.example.ChaoticDeck.Model.Player.Player;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public class playerRepository {

    private final JdbcTemplate jdbcTemplate;

    // RowMapper แปลงแต่ละแถวของ ResultSet เป็น Player object
    private final RowMapper<Player> playerRowMapper = (rs, rowNum) -> {
        Player player = new Player();
        player.setId(rs.getLong("id"));
        player.setName(rs.getString("name"));
        return player;
    };

    public playerRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Player> pullPlayerData() {
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