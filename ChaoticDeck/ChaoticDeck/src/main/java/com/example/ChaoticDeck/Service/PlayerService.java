package com.example.ChaoticDeck.Service;

import com.example.ChaoticDeck.repository.playerRepository;
import com.example.ChaoticDeck.Model.Player.Player;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlayerService {

    private final playerRepository playerRepository;

    public PlayerService(playerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    // ดึงข้อมูลทั้งหมด
    public List<Player> getAllPlayers() {
        return playerRepository.pullPlayerData();
    }

    // ดึงข้อมูลตาม id
    public Player getPlayerById(Long id) {
        return playerRepository.findById(id);
    }

    // เพิ่มข้อมูลใหม่
    public Player createPlayer(Player player) {
        playerRepository.save(player);
        return player;
    }
}