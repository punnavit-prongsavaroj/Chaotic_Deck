package com.example.ChaoticDeck.controller;

import com.example.ChaoticDeck.Model.Player.Player;
import com.example.ChaoticDeck.Service.GameService;
import com.example.ChaoticDeck.Service.PlayerService;
import com.example.ChaoticDeck.Service.RoomService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Player")
public class PlayerController {

    private final PlayerService playerService;
 
    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }
 
    // สร้างผู้เล่นใหม่ (กรอกชื่อตอนเปิดแอป)
    @PostMapping
    public Player createPlayer(@RequestBody Player request) {
        Player player = new Player();
        player.setName(request.getName());   // set เฉพาะ field ที่อนุญาต
        return playerService.createPlayer(player);
    }
 
    // ดูข้อมูลผู้เล่น
    @GetMapping("/{id}")
    public Player getPlayer(@PathVariable Long id) {
        return playerService.getPlayerById(id);
    }

    

}