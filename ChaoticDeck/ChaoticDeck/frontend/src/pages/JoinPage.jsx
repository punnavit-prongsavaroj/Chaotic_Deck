import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { getPlayerId } from '../utils/cookie';
import { joinRoom } from '../api/api';

export default function JoinPage() {
  const [roomId, setRoomId] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const playerId = getPlayerId();

  if (!playerId) {
    navigate('/', { replace: true });
    return null;
  }

  async function handleJoin(e) {
    e.preventDefault();
    const code = roomId.trim().toUpperCase();
    if (!code) {
      setError('กรุณากรอกรหัสห้อง');
      return;
    }

    setLoading(true);
    setError('');

    try {
      await joinRoom(code, playerId);
      navigate(`/room/${code}`);
    } catch (err) {
      setError(err.message || 'ไม่สามารถเข้าร่วมห้องได้');
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="card-container">
      <h1 className="logo-title">🚪 เข้าร่วมห้อง</h1>
      <p className="subtitle">กรอกรหัสห้อง 6 หลักที่เพื่อนแชร์มา</p>

      {error && <div className="error-banner">❌ {error}</div>}

      <form onSubmit={handleJoin}>
        <div className="form-group">
          <label className="form-label" htmlFor="roomIdInput" style={{ textAlign: 'center' }}>
            รหัสห้อง (Room Code)
          </label>
          <input
            id="roomIdInput"
            type="text"
            className="input-field uppercase"
            placeholder="เช่น A3K9X2"
            value={roomId}
            onChange={(e) => setRoomId(e.target.value.toUpperCase())}
            maxLength={10}
            disabled={loading}
            autoFocus
          />
        </div>

        <div className="btn-group">
          <button
            type="submit"
            className="btn btn-secondary"
            disabled={loading || !roomId.trim()}
          >
            {loading ? 'กำลังตรวจสอบ...' : '🚀 เข้าร่วมห้อง'}
          </button>

          <button
            type="button"
            className="btn btn-outline"
            onClick={() => navigate('/lobby')}
            disabled={loading}
          >
            ← ย้อนกลับ
          </button>
        </div>
      </form>
    </div>
  );
}
