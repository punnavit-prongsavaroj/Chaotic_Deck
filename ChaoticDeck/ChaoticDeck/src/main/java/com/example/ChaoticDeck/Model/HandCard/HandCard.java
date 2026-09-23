package com.example.ChaoticDeck.Model.HandCard;

import com.example.ChaoticDeck.Model.Card.Card;
import com.example.ChaoticDeck.Model.Player.Player;

public class HandCard {

    private int id;
    private Player player;
    private Card card;
    private int amount;

    // ── Constructors ──────────────────────────────────────
    public HandCard() {}

    public HandCard(Player player, Card card, int amount) {
        this.player = player;
        this.card = card;
        this.amount = amount;
    }

    public HandCard(int id, Player player, Card card, int amount) {
        this.id = id;
        this.player = player;
        this.card = card;
        this.amount = amount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}