package com.example.ChaoticDeck.Model.Player;

import com.example.ChaoticDeck.Model.HandCard.HandCard;

public class Player {

    private double id;
    private String name;


    // ── Constructors ──────────────────────────────────────
    public Player() {}


    public Player(double id, String name) {
        this.id = id;
        this.name = name;
    }


    public double getId() {
        return id;
    }


    public void setId(double id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }

    

    


}