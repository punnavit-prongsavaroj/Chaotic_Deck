package com.example.ChaoticDeck.Controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.ChaoticDeck.Service.GameService;

@RestController 
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }
    
    // เริ่มเกมในห้อง
    @PostMapping("/{roomId}/start")
    public String startGame(@PathVariable Long roomId) {
        return gameService.startGame(roomId);
    }
 
    // จั่วการ์ด
    @PostMapping("/{roomId}/draw")
    public String drawCard(@PathVariable Long roomId, @RequestParam Long playerId) {
        return gameService.drawCard(roomId, playerId);
    }

}
