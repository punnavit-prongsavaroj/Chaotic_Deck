const API_URL = 'http://localhost:8080';

export async function createPlayer(name: string) {
    const res = await fetch(`${API_URL}/Player`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ name })
    });
    return res.json();
}

export async function createRoom(playerId: number, maxPlayers: number = 4) {
    const res = await fetch(`${API_URL}/Room/create?playerId=${playerId}&maxPlayers=${maxPlayers}`, {
        method: 'POST'
    });
    if (!res.ok) throw new Error("Failed to create room");
    return res.json();
}

export async function joinRoom(roomId: string, playerId: number) {
    const res = await fetch(`${API_URL}/Room/${roomId}/join?playerId=${playerId}`, {
        method: 'POST'
    });
    if (!res.ok) throw new Error("Failed to join room");
}

export async function getRoomDetails(roomId: string) {
    const res = await fetch(`${API_URL}/Room/${roomId}`);
    return res.json();
}

export async function getGameState(roomId: string) {
    const res = await fetch(`${API_URL}/${roomId}/state`);
    return res.json();
}

export async function getHand(roomId: string, playerId: number) {
    const res = await fetch(`${API_URL}/${roomId}/hand/${playerId}`);
    return res.json();
}

export async function startGame(roomId: string, playerId: number) {
    const res = await fetch(`${API_URL}/${roomId}/start?playerId=${playerId}`, {
        method: 'POST'
    });
    return res.text();
}

export async function defuseBomb(roomId: string, playerId: number, putAtPosition: number = 0) {
    const res = await fetch(`${API_URL}/${roomId}/defuse?playerId=${playerId}&putAtPosition=${putAtPosition}`, {
        method: 'POST'
    });
    return res.text();
}

export async function giveFavor(roomId: string, playerId: number, cardId: number) {
    const res = await fetch(`${API_URL}/${roomId}/give-favor?playerId=${playerId}&cardId=${cardId}`, {
        method: 'POST'
    });
    return res.text();
}

export async function drawCard(roomId: string, playerId: number) {
    const res = await fetch(`${API_URL}/${roomId}/draw?playerId=${playerId}`, {
        method: 'POST'
    });
    return res.text();
}

export async function playCards(roomId: string, playerId: number, cardIds: number[], cardType: string, targetPlayerId?: number, retrieveCardId?: number, targetCardName?: string) {
    let url = `${API_URL}/${roomId}/play?playerId=${playerId}&cardIds=${cardIds.join(',')}&cardType=${cardType}`;
    if (targetPlayerId) url += `&targetPlayerId=${targetPlayerId}`;
    if (retrieveCardId) url += `&retrieveCardId=${retrieveCardId}`;
    if (targetCardName) url += `&targetCardName=${targetCardName}`;
    
    const res = await fetch(url, { method: 'POST' });
    return res.text();
}

export async function getDiscardPile(roomId: string) {
    const res = await fetch(`${API_URL}/${roomId}/discard`);
    return res.json();
}

export async function getSeeTheFuture(roomId: string) {
    const res = await fetch(`${API_URL}/${roomId}/seethefuture`);
    return res.json();
}

export const leaveRoom = async (roomId: string, playerId: number) => {
  const res = await fetch(`${API_URL}/Room/${roomId}/leave?playerId=${playerId}`, { method: 'POST' })
  if (!res.ok) throw new Error("Failed to leave room")
  return res.text()
}
