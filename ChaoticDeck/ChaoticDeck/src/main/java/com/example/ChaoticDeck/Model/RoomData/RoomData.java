package com.example.ChaoticDeck.Model.RoomData;

import com.example.ChaoticDeck.Model.BOMB.BOMB;
import com.example.ChaoticDeck.Model.DeckList.DeckList;
import com.example.ChaoticDeck.Model.TOP3.TOP3;
import com.example.ChaoticDeck.Model.PlayerinRoom.PlayerinRoom;

import java.util.List;

public class RoomData {
    private int id;
    private String RoomID;
    private int ledder_id;
    private int Top3Count;
    private TOP3 TOP3;
    private List<BOMB> BOMBs;
    private DeckList DeckList;
    private PlayerinRoom PlayerinRoom;

    // ── Constructors ──────────────────────────────────────
    public RoomData() {}

    public RoomData(int id, String roomID, int ledder_id, int top3Count, com.example.ChaoticDeck.Model.TOP3.TOP3 tOP3,
            List<com.example.ChaoticDeck.Model.BOMB.BOMB> bOMBs, com.example.ChaoticDeck.Model.DeckList.DeckList deckList,
            PlayerinRoom playerinRoom) {
        this.id = id;
        RoomID = roomID;
        this.ledder_id = ledder_id;
        Top3Count = top3Count;
        TOP3 = tOP3;
        BOMBs = bOMBs;
        DeckList = deckList;
        PlayerinRoom = playerinRoom;
    }

    public RoomData(String roomID, int top3Count, com.example.ChaoticDeck.Model.TOP3.TOP3 tOP3,
            List<com.example.ChaoticDeck.Model.BOMB.BOMB> bOMBs, com.example.ChaoticDeck.Model.DeckList.DeckList deckList) {
        RoomID = roomID;
        Top3Count = top3Count;
        TOP3 = tOP3;
        BOMBs = bOMBs;
        DeckList = deckList;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRoomID() {
        return RoomID;
    }

    public void setRoomID(String roomID) {
        RoomID = roomID;
    }

    public int getTop3Count() {
        return Top3Count;
    }

    public void setTop3Count(int top3Count) {
        Top3Count = top3Count;
    }

    public TOP3 getTOP3() {
        return TOP3;
    }

    public void setTOP3(TOP3 tOP3) {
        TOP3 = tOP3;
    }

    public List<BOMB> getBOMBs() {
        return BOMBs;
    }

    public void setBOMBs(List<BOMB> bOMBs) {
        BOMBs = bOMBs;
    }

    public DeckList getDeckList() {
        return DeckList;
    }

    public void setDeckList(DeckList deckList) {
        DeckList = deckList;
    }

	public int getLedder_id() {
		return ledder_id;
	}

	public void setLedder_id(int ledder_id) {
		this.ledder_id = ledder_id;
	}

    public PlayerinRoom getPlayerinRoom() {
        return PlayerinRoom;
    }

    public void setPlayerinRoom(PlayerinRoom playerinRoom) {
        PlayerinRoom = playerinRoom;
    }
}