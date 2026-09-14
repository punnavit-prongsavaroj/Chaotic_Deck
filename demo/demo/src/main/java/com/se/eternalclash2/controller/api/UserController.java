package com.se.eternalclash2.controller.api;

import com.se.eternalclash2.dto.request.UserCreateReq;
import com.se.eternalclash2.dto.response.UserRes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Users", description = "User management APIs")
public class UserController {

    @PostMapping
    @Operation(summary = "Register new user")
    public ResponseEntity<UserRes> register(@Valid @RequestBody UserCreateReq req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new UserRes());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    public ResponseEntity<UserRes> getUser(@PathVariable String id) {
        return ResponseEntity.ok(new UserRes());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable String id) {
    }

    @GetMapping
    @Operation(summary = "Get users with pagination")
    public ResponseEntity<List<UserRes>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "stats.wins,desc") String sort) {
        return ResponseEntity.ok(List.of());
    }
}
