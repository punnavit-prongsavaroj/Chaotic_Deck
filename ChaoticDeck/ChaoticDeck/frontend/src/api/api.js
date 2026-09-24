// API Service ติดต่อกับ Spring Boot Backend
const BASE_URL = ''; // ใช้ relative URL เพื่อให้ผ่าน Vite proxy หรือ domain เดียวกัน

export async function createPlayer(name) {
  const res = await fetch(`${BASE_URL}/Player`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ name }),
  });
  if (!res.ok) throw new Error('ไม่สามารถสร้างผู้เล่นได้');
  return res.json();
}

export async function checkPlayerExists(id) {
  try {
    const res = await fetch(`${BASE_URL}/Player/${id}/exists`);
    if (!res.ok) return false;
    return await res.json();
  } catch {
    return false;
  }
}

export async function getPlayerRoom(id) {
  try {
    const res = await fetch(`${BASE_URL}/Player/${id}/room`);
    if (res.status === 204) return null;
    if (!res.ok) return null;
    const data = await res.json();
    return data.roomId || null;
  } catch {
    return null;
  }
}

export async function createRoom(playerId, maxPlayers = 4) {
  const res = await fetch(`${BASE_URL}/Room/create?playerId=${playerId}&maxPlayers=${maxPlayers}`, {
    method: 'POST',
  });
  if (!res.ok) {
    const errText = await res.text();
    throw new Error(errText || 'ไม่สามารถสร้างห้องได้');
  }
  return res.json();
}

export async function joinRoom(roomId, playerId) {
  const res = await fetch(`${BASE_URL}/Room/${roomId}/join?playerId=${playerId}`, {
    method: 'POST',
  });
  if (!res.ok) {
    let msg = 'ไม่สามารถเข้าร่วมห้องได้';
    try {
      const data = await res.json();
      msg = data.message || msg;
    } catch {
      const text = await res.text();
      if (text) msg = text;
    }
    throw new Error(msg);
  }
  return true;
}

export async function leaveRoom(roomId, playerId) {
  try {
    await fetch(`${BASE_URL}/Room/${roomId}/leave?playerId=${playerId}`, {
      method: 'POST',
    });
  } catch (err) {
    console.warn('Error leaving room:', err);
  }
}

export async function getRoomDetails(roomId) {
  const res = await fetch(`${BASE_URL}/Room/${roomId}`);
  if (!res.ok) {
    throw new Error('ไม่พบห้องนี้หรือห้องถูกยุบแล้ว');
  }
  return res.json();
}

export async function startGame(roomId) {
  const res = await fetch(`${BASE_URL}/${roomId}/start`, {
    method: 'POST',
  });
  if (!res.ok) {
    throw new Error('ไม่สามารถเริ่มเกมได้');
  }
  return res.text();
}
