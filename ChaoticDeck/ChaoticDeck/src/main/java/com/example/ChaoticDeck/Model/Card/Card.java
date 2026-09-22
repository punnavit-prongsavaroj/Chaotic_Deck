package com.example.ChaoticDeck.Model.Card;



public abstract class Card {
    
    private String Type;
    private String name;


    // ── Constructors ──────────────────────────────────────
    public Card() {}

    public Card(String Type,String name) {
        this.Type = Type;
        this.name = name;
    }

    // ── Getters & Setters ─────────────────────────────────
    public String  getType(){ return Type; }
    public void    setType(String Type){ this.Type = Type; }

    public String  getName()              { return name; }
    public void    setName(String name)   { this.name = name; }

}