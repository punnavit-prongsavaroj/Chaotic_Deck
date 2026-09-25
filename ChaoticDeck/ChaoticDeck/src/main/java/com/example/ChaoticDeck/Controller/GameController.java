package com.example.ChaoticDeck.Controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.ChaoticDeck.Service.GameService;
import com.example.ChaoticDeck.Service.RoomService;

@RestController
@CrossOrigin(origins = "*")
public class GameController {

    private final GameService gameService;
    private final RoomService roomService;

    public GameController(GameService gameService, RoomService roomService) {
        this.gameService = gameService;
        this.roomService = roomService;
    }
    
    // เริ่มเกมในห้อง
    @PostMapping("/{roomId}/start")
    public String startGame(@PathVariable String roomId) {
        gameService.startGame(roomId);
        return "Started";
    }
 
    // จั่วการ์ด
    @PostMapping("/{roomId}/draw")
    public String drawCard(@PathVariable String roomId, @RequestParam Long playerId) {
        return gameService.drawCard(roomId, playerId);
    }

    // ลงการ์ด
    @PostMapping("/{roomId}/play")
    public String playCard(@PathVariable String roomId, 
                           @RequestParam Long playerId, 
                           @RequestParam List<Integer> cardIds, 
                           @RequestParam String cardType,
                           @RequestParam(required = false) Long targetPlayerId) {
        return gameService.playCards(roomId, playerId, cardIds, cardType, targetPlayerId);
    }

    // มอบการ์ดให้เพื่อน (เมื่อโดน FAVOR)
    @PostMapping("/{roomId}/give-favor")
    public String giveFavor(@PathVariable String roomId, @RequestParam Long playerId, @RequestParam int cardId) {
        return gameService.giveFavor(roomId, playerId, cardId);
    }

    // ปลดชนวนระเบิด
    @PostMapping("/{roomId}/defuse")
    public String defuseBomb(@PathVariable String roomId, @RequestParam Long playerId, @RequestParam int putAtPosition) {
        return gameService.defuseBomb(roomId, playerId, putAtPosition);
    }

}
