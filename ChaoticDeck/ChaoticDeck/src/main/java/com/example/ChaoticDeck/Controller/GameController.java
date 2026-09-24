package com.example.ChaoticDeck.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.ChaoticDeck.service.GameService;

@RestController 
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }
    
}
