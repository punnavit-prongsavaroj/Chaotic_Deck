package com.example.ChaoticDeck.Controller;

import com.example.ChaoticDeck.Model.Player.Player;
import com.example.ChaoticDeck.Service.PlayerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/players")
public class PlayerController {

    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    // GET http://localhost:8080/api/players
    @GetMapping
    public List<Player> getAllPlayers() {
        return playerService.getAllPlayers();
    }

    // GET http://localhost:8080/api/players/1
    @GetMapping("/{id}")
    public Player getPlayerById(@PathVariable Long id) {
        return playerService.getPlayerById(id);
    }

    // POST http://localhost:8080/api/players
    // Body (JSON): { "name": "Zad" }
    @PostMapping
    public Player createPlayer(@RequestBody Player player) {
        return playerService.createPlayer(player);
    }
}