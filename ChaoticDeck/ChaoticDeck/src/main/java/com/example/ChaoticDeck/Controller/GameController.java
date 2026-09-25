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
        List<Long> players = roomService.getPlayersInRoom(roomId);
        gameService.startGame(roomId, players.size());
        return "Started";
    }
 
    // จั่วการ์ด
    @PostMapping("/{roomId}/draw")
    public String drawCard(@PathVariable String roomId, @RequestParam Long playerId) {
        return gameService.drawCard(roomId, playerId);
    }

}
