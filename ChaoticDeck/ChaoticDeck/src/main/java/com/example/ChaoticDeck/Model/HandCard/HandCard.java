package com.example.ChaoticDeck.Model.HandCard;

import com.example.ChaoticDeck.Model.Card.Card;
import com.example.ChaoticDeck.Model.Player.Player;

public class HandCard {

    private Card card;
    private int Amount;
    private int id;
    private Player player;

    


    // ── Constructors ──────────────────────────────────────
    public HandCard() {}

    
    public HandCard(Card card, int amount) {
        this.card = card;
        Amount = amount;
    }

    public HandCard(Card card, int amount, int id, Player player) {
        this.card = card;
        Amount = amount;
        this.id = id;
        this.player = player;
    }


    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public int getAmount() {
        return Amount;
    }

    public void setAmount(int amount) {
        Amount = amount;
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


    
}