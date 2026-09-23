package com.example.ChaoticDeck.Model.Card;



public abstract class Card {
    
    private int id;
    private String Type;
    private String name;


    // ── Constructors ──────────────────────────────────────
    public Card() {}

    public Card(int id, String Type,String name) {
        this.id = id;
        this.Type = Type;
        this.name = name;
    }

    public Card(String Type,String name) {
        this.Type = Type;
        this.name = name;
    }

    // ── Getters & Setters ─────────────────────────────────
    public int     getId()                { return id; }
    public void    setId(int id)          { this.id = id; }

    public String  getType(){ return Type; }
    public void    setType(String Type){ this.Type = Type; }

    public String  getName()              { return name; }
    public void    setName(String name)   { this.name = name; }

}