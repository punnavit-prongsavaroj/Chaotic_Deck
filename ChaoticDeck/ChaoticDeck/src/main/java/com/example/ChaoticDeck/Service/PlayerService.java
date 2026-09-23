package com.example.ChaoticDeck.service;

import com.example.ChaoticDeck.repository.PlayerRepository;
import com.example.ChaoticDeck.Model.Player.Player;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {
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
        playerRepository.add(player);
        return player;
    }
}