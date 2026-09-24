import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { getPlayerId, getPlayerName, clearSession } from '../utils/cookie';
import { getPlayerRoom, createRoom } from '../api/api';

export default function LobbyPage() {
  const [maxPlayers, setMaxPlayers] = useState(4);
  const [loading, setLoading] = useState(false);
  const [checkingRoom, setCheckingRoom] = useState(true);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const playerId = getPlayerId();
  const playerName = getPlayerName();

  useEffect(() => {
    if (!playerId) {
      navigate('/', { replace: true });
      return;
    }

    // Auto-rejoin: เช็คว่าผู้เล่นคนนี้เคยอยู่ในห้องไหนค้างไว้หรือไม่
    async function checkAutoRejoin() {
      try {
        const roomId = await getPlayerRoom(playerId);
        if (roomId) {
          // หากอยู่ในห้องอยู่แล้ว พาเข้าห้องนั้นอัตโนมัติทันที
          navigate(`/room/${roomId}`, { replace: true });
          return;
        }
      } catch (err) {
        console.warn('Auto rejoin check failed:', err);
      } finally {
        setCheckingRoom(false);
      }
    }

    checkAutoRejoin();
  }, [playerId, navigate]);

  async function handleCreateRoom() {
    setLoading(true);
    setError('');
    try {
      const room = await createRoom(playerId, maxPlayers);
      navigate(`/room/${room.roomID || room.roomId}`);
    } catch (err) {
      setError(err.message || 'ไม่สามารถสร้างห้องได้');
      setLoading(false);
    }
  }

  function handleLogout() {
    clearSession();
    navigate('/');
  }

  if (checkingRoom) {
    return (
      <div className="card-container">
        <h2 className="logo-title">Chaotic Deck</h2>
        <p className="subtitle">กำลังตรวจสอบสถานะห้อง...</p>
      </div>
    );
  }

  return (
    <div className="card-container">
      <h1 className="logo-title">🎲 ล็อบบี้</h1>
      <p className="subtitle">
        ยินดีต้อนรับ, <strong style={{ color: '#ff7675' }}>{playerName || `Player #${playerId}`}</strong> 👋
      </p>

      {error && <div className="error-banner">⚠️ {error}</div>}

      <div className="select-group">
        <span className="form-label" style={{ margin: 0 }}>
          ผู้เล่นสูงสุดต่อห้อง:
        </span>
        <select
          className="select-control"
          value={maxPlayers}
          onChange={(e) => setMaxPlayers(parseInt(e.target.value, 10))}
          disabled={loading}
        >
          <option value={2}>2 คน</option>
          <option value={3}>3 คน</option>
          <option value={4}>4 คน (มาตรฐาน)</option>
          <option value={5}>5 คน</option>
          <option value={6}>6 คน</option>
          <option value={7}>7 คน</option>
          <option value={8}>8 คน</option>
        </select>
      </div>

      <div className="btn-group">
        <button
          className="btn btn-primary"
          onClick={handleCreateRoom}
          disabled={loading}
        >
          {loading ? 'กำลังสร้างห้อง...' : '🏠 สร้างห้องใหม่'}
        </button>

        <button
          className="btn btn-secondary"
          onClick={() => navigate('/join')}
          disabled={loading}
        >
          🚪 เข้าร่วมห้องด้วยรหัส
        </button>

        <button
          className="btn btn-outline"
          onClick={handleLogout}
          disabled={loading}
          style={{ marginTop: 8 }}
        >
          เปลี่ยนชื่อ / ออกจากระบบ
        </button>
      </div>
    </div>
  );
}
