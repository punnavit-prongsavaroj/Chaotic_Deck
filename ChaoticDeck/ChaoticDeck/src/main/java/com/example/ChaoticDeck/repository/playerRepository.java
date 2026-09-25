package com.example.ChaoticDeck.repository;

import com.example.ChaoticDeck.Model.Player.Player;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class PlayerRepository {

    private final JdbcTemplate jdbcTemplate;

    // RowMapper แปลงแต่ละแถวของ ResultSet เป็น Player object
    private final RowMapper<Player> playerRowMapper = (rs, rowNum) -> {
        Player player = new Player();
        player.setId(rs.getLong("id"));
        player.setName(rs.getString("name"));
        return player;
    };

    public PlayerRepository(JdbcTemplate jdbcTemplate) {
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

    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM player WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    public long add(Player player) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "INSERT INTO player (name) VALUES (?)";
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, player.getName());
            return ps;
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }

    public int delte(Player player) {
        String sql = "DELETE FROM player WHERE name = ?";
        return jdbcTemplate.update(sql, player.getName());
    }
}