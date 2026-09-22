package com.example.ChaoticDeck.Model.BOMB;

public class BOMB {

    private int BombCount;

    // ── Constructors ──────────────────────────────────────
    public BOMB() {}

    public BOMB(int BombCount) {
        this.BombCount = BombCount;
    }

    // ── Getters & Setters ─────────────────────────────────
    public int getBombCount(){ return BombCount; }
    public void setBombCount(int BombCount){ this.BombCount = BombCount; }
}