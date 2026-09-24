package com.example.ChaoticDeck.controller;

import com.example.ChaoticDeck.service.GameService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/game")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/{roomId}/start")
    public String startGame(@PathVariable String roomId, @RequestParam int playerCount) {
        gameService.startGame(roomId, playerCount);
        return "Game started in room: " + roomId;
    }

    @PostMapping("/{roomId}/draw")
    public String drawCard(@PathVariable String roomId, @RequestParam long playerId) {
        return gameService.drawCard(roomId, playerId);
    }

    @PostMapping("/{roomId}/play")
    public String playCard(@PathVariable String roomId, @RequestParam long playerId, @RequestParam String cardType) {
        gameService.playCard(roomId, playerId, cardType);
        return "Played " + cardType;
    }
}
