package com.se.eternalclash2.controller.api;

import com.se.eternalclash2.dto.request.ActionDeclareReq;
import com.se.eternalclash2.dto.request.RoomCreateReq;
import com.se.eternalclash2.dto.response.RoomRes;
import com.se.eternalclash2.validation.ValidRoomCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/rooms")
@Tag(name = "Rooms", description = "Game room and action APIs")
public class RoomController {

    @PostMapping
    @Operation(summary = "Create game room")
    public ResponseEntity<RoomRes> createRoom(@Valid @RequestBody RoomCreateReq req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new RoomRes());
    }

    @GetMapping("/{roomCode}")
    @Operation(summary = "Get room details")
    public ResponseEntity<RoomRes> getRoom(@PathVariable @ValidRoomCode String roomCode) {
        return ResponseEntity.ok(new RoomRes());
    }

    @PostMapping("/{roomCode}/players")
    @Operation(summary = "Join room")
    public ResponseEntity<Void> joinRoom(@PathVariable String roomCode) {
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{roomCode}/players/{id}")
    @Operation(summary = "Leave room")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void leaveRoom(@PathVariable String roomCode, @PathVariable String id) {
    }

    @PostMapping("/{roomCode}/start")
    @Operation(summary = "Start game")
    public ResponseEntity<Void> startGame(@PathVariable String roomCode) {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{roomCode}/actions")
    @Operation(summary = "Declare action")
    public ResponseEntity<Void> declareAction(
            @PathVariable String roomCode, 
            @Valid @RequestBody ActionDeclareReq req) {
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/{roomCode}/actions/{id}/challenge")
    @Operation(summary = "Challenge an action")
    public ResponseEntity<Void> challengeAction(@PathVariable String roomCode, @PathVariable String id) {
        return ResponseEntity.ok().build();
    }
}
