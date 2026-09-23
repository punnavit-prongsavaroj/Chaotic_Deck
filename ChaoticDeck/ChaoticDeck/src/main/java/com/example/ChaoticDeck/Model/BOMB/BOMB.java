package com.example.ChaoticDeck.Model.BOMB;

public class BOMB {

    private int id;
    private String roomId; 
    private int bombCount; // -1 = อยู่ในกองสุ่ม (ยังไม่ระบุตำแหน่ง), >0 = นัับถอยหลัง (ระบุตำแหน่งแล้ว)

    // ── Constructors ──────────────────────────────────────
    public BOMB() {}

    public BOMB(int id, String roomId, int bombCount) {
        this.id = id;
        this.roomId = roomId;
        this.bombCount = bombCount;
    }

    public BOMB(String roomId, int bombCount) {
        this.roomId = roomId;
        this.bombCount = bombCount;
    }

    // ── Getters & Setters ─────────────────────────────────
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }

    public int getBombCount(){ return bombCount; }
    public void setBombCount(int bombCount){ this.bombCount = bombCount; }
}