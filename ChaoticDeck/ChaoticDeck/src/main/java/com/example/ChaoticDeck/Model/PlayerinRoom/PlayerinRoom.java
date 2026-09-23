package com.example.ChaoticDeck.Model.PlayerinRoom;

import java.util.List;
import com.example.ChaoticDeck.Model.Player.Player;

public class PlayerinRoom {

    private int id;
    private List<Player> PlayerinRoom;

    // ── Constructors ──────────────────────────────────────
    public PlayerinRoom() {}

    public PlayerinRoom(int id, List<Player> playerinRoom) {
        this.id = id;
        this.PlayerinRoom = playerinRoom;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

	public List<Player> getPlayerinRoom() {
		return PlayerinRoom;
	}

	public void setPlayerinRoom(List<Player> playerinRoom) {
		PlayerinRoom = playerinRoom;
	}

    public void addPlayerinRoom(Player player) {
		PlayerinRoom.add(player);
	}
}