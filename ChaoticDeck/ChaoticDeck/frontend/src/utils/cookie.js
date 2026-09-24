// จัดการ Cookie สำหรับ Player ID
export function getCookie(name) {
  const match = document.cookie.match(new RegExp('(^| )' + name + '=([^;]+)'));
  if (match) return decodeURIComponent(match[2]);
  // fallback ไปยัง localStorage เผื่อกรณี cookie โดน browser บางตัวบล็อก
  return localStorage.getItem(name);
}

export function setCookie(name, value, days = 30) {
  const date = new Date();
  date.setTime(date.getTime() + days * 24 * 60 * 60 * 1000);
  const expires = '; expires=' + date.toUTCString();
  document.cookie = name + '=' + encodeURIComponent(value) + expires + '; path=/; SameSite=Lax';
  localStorage.setItem(name, value);
}

export function removeCookie(name) {
  document.cookie = name + '=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;';
  localStorage.removeItem(name);
}

export function getPlayerId() {
  const id = getCookie('chaotic_player_id');
  return id ? parseInt(id, 10) : null;
}

export function setPlayerId(id) {
  setCookie('chaotic_player_id', id.toString(), 30);
}

export function getPlayerName() {
  return getCookie('chaotic_player_name') || '';
}

export function setPlayerName(name) {
  setCookie('chaotic_player_name', name, 30);
}

export function clearSession() {
  removeCookie('chaotic_player_id');
  removeCookie('chaotic_player_name');
}
