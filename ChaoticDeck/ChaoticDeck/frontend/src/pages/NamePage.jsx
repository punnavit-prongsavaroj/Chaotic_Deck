import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { getPlayerId, setPlayerId, setPlayerName, clearSession } from '../utils/cookie';
import { createPlayer, checkPlayerExists } from '../api/api';

export default function NamePage() {
  const [name, setName] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    async function checkCookie() {
      const id = getPlayerId();
      if (id) {
        setLoading(true);
        const exists = await checkPlayerExists(id);
        if (exists) {
          navigate('/lobby', { replace: true });
          return;
        } else {
          clearSession();
        }
        setLoading(false);
      }
    }
    checkCookie();
  }, [navigate]);

  async function handleSubmit(e) {
    e.preventDefault();
    const trimmed = name.trim();
    if (!trimmed) {
      setError('กรุณากรอกชื่อผู้เล่น');
      return;
    }

    setLoading(true);
    setError('');

    try {
      const player = await createPlayer(trimmed);
      setPlayerId(player.id);
      setPlayerName(player.name || trimmed);
      navigate('/lobby');
    } catch (err) {
      setError(err.message || 'เกิดข้อผิดพลาดในการสร้างผู้เล่น');
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="card-container">
      <h1 className="logo-title">🎴 Chaotic Deck</h1>
      <p className="subtitle">เกมการ์ดสุดป่วน วางแผน หรือ โดนระเบิด!</p>

      {error && <div className="error-banner">⚠️ {error}</div>}

      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label className="form-label" htmlFor="playerName">
            ชื่อของคุณ
          </label>
          <input
            id="playerName"
            type="text"
            className="input-field"
            placeholder="เช่น Pizza, MasterCat, John..."
            value={name}
            onChange={(e) => setName(e.target.value)}
            disabled={loading}
            maxLength={20}
            autoFocus
          />
        </div>

        <button type="submit" className="btn btn-primary" disabled={loading || !name.trim()}>
          {loading ? 'กำลังเข้าเกม...' : '🚀 เข้าสู่เกม'}
        </button>
      </form>
    </div>
  );
}
