package com.example.ChaoticDeck.Controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
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
    
    // เริ่มเกมในห้อง (หัวหน้าห้องเท่านั้น)
    @PostMapping("/{roomId}/start")
    public String startGame(@PathVariable String roomId, @RequestParam Long playerId) {
        return gameService.startGame(roomId, playerId);
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
                           @RequestParam(required = false) Long targetPlayerId,
                           @RequestParam(required = false) Integer retrieveCardId,
                           @RequestParam(required = false) String targetCardName) {
        return gameService.playCards(roomId, playerId, cardIds, cardType, targetPlayerId, retrieveCardId, targetCardName);
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

    // ดูอนาคต 3 ใบ
    @GetMapping("/{roomId}/seethefuture")
    public List<com.example.ChaoticDeck.Model.TOP3.TOP3> seeTheFuture(@PathVariable String roomId) {
        return gameService.getTop3(roomId);
    }

    // ดูไพ่บนมือผู้เล่น
    @GetMapping("/{roomId}/hand/{playerId}")
    public List<com.example.ChaoticDeck.Model.HandCard.HandCard> getHand(@PathVariable String roomId, @PathVariable Long playerId) {
        return gameService.getHand(roomId, playerId);
    }

    // ดูสถานะเกมปัจจุบันของห้อง
    @GetMapping("/{roomId}/state")
    public java.util.Map<String, Object> getGameState(@PathVariable String roomId) {
        return gameService.getGameState(roomId);
    }

    // ดูกองไพ่ทิ้ง
    @GetMapping("/{roomId}/discard")
    public List<com.example.ChaoticDeck.Model.DiscardPile.DiscardPile> getDiscardPile(@PathVariable String roomId) {
        return gameService.getDiscardPile(roomId);
    }

}
