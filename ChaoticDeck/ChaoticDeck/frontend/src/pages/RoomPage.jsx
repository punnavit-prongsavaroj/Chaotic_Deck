import React, { useState, useEffect, useRef } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { getPlayerId } from '../utils/cookie';
import { getRoomDetails, leaveRoom, startGame } from '../api/api';

export default function RoomPage() {
  const { roomId } = useParams();
  const [roomData, setRoomData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [copied, setCopied] = useState(false);
  const [starting, setStarting] = useState(false);
  const navigate = useNavigate();

  const playerId = getPlayerId();
  const pollingRef = useRef(null);

  useEffect(() => {
    if (!playerId) {
      navigate('/', { replace: true });
      return;
    }

    async function fetchRoom() {
      try {
        const data = await getRoomDetails(roomId);
        setRoomData(data);
        setLoading(false);

        // ถ้าสถานะห้องเปลี่ยนเป็น PLAYING
        if (data.status === 'PLAYING') {
          // สามารถต่อยอดไปหน้ากระดานเกมในอนาคต
        }
      } catch (err) {
        // หากไม่พบห้อง (เช่น Leader ออกแล้วห้องถูกยุบ)
        clearInterval(pollingRef.current);
        alert('ห้องนี้ถูกยุบหรือปิดไปแล้ว เนื่องจากหัวหน้าห้องออกจากห้อง');
        navigate('/lobby', { replace: true });
      }
    }

    fetchRoom();

    // Polling ทุกๆ 2 วินาทีเพื่ออัปเดตข้อมูลและรายชื่อผู้เล่นแบบ Real-time
    pollingRef.current = setInterval(fetchRoom, 2000);

    return () => {
      if (pollingRef.current) clearInterval(pollingRef.current);
    };
  }, [roomId, playerId, navigate]);

  function handleCopy() {
    navigator.clipboard.writeText(roomId);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  }

  async function handleLeave() {
    if (confirm('คุณแน่ใจหรือไม่ว่าต้องการออกจากห้อง?')) {
      if (pollingRef.current) clearInterval(pollingRef.current);
      await leaveRoom(roomId, playerId);
      navigate('/lobby');
    }
  }

  async function handleStartGame() {
    setStarting(true);
    setError('');
    try {
      await startGame(roomId);
      // reload room data
      const data = await getRoomDetails(roomId);
      setRoomData(data);
    } catch (err) {
      setError(err.message || 'ไม่สามารถเริ่มเกมได้');
    } finally {
      setStarting(false);
    }
  }

  if (loading) {
    return (
      <div className="card-container">
        <h2 className="logo-title">Chaotic Deck</h2>
        <p className="subtitle">กำลังโหลดข้อมูลห้อง...</p>
      </div>
    );
  }

  if (!roomData) return null;

  const isLeader = roomData.leaderId === playerId;
  const players = roomData.players || [];
  const maxPlayers = roomData.maxPlayers || 4;
  const emptySlots = Math.max(0, maxPlayers - players.length);

  return (
    <div className="card-container">
      <h1 className="logo-title">ห้องรอเล่นเกม</h1>

      <div className="room-code-box">
        <div>
          <span style={{ fontSize: '0.8rem', color: '#a0a0be', display: 'block' }}>รหัสห้อง</span>
          <span className="room-code">{roomId}</span>
        </div>
        <button className="copy-btn" onClick={handleCopy}>
          {copied ? '✅ คัดลอกแล้ว' : '📋 คัดลอกรหัส'}
        </button>
      </div>

      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 12 }}>
        <span className="badge badge-waiting">
          สถานะ: {roomData.status === 'PLAYING' ? 'กำลังเล่นเกม ⚔️' : 'กำลังรอผู้เล่น... ⏳'}
        </span>
        <span className="badge">
          ผู้เล่น {players.length} / {maxPlayers} คน
        </span>
      </div>

      {error && <div className="error-banner">⚠️ {error}</div>}

      <div className="player-list">
        {players.map((p) => {
          const isMe = p.id === playerId;
          return (
            <div key={p.id} className={`player-item ${isMe ? 'me' : ''}`}>
              <div className="player-info">
                <div className="player-avatar">
                  {p.name ? p.name.charAt(0).toUpperCase() : '?'}
                </div>
                <span style={{ fontWeight: 600 }}>
                  {p.name} {isMe && <span style={{ color: '#ff7675' }}>(คุณ)</span>}
                </span>
              </div>
              <div>
                {p.isLeader && <span className="badge badge-leader">👑 หัวหน้าห้อง</span>}
              </div>
            </div>
          );
        })}

        {/* ช่องว่างรอผู้เล่น */}
        {Array.from({ length: emptySlots }).map((_, idx) => (
          <div
            key={`empty-${idx}`}
            className="player-item"
            style={{ opacity: 0.4, borderStyle: 'dashed' }}
          >
            <div className="player-info">
              <div className="player-avatar" style={{ background: '#333' }}>+</div>
              <span style={{ color: '#888', fontStyle: 'italic' }}>รอผู้เล่นเข้าร่วม...</span>
            </div>
          </div>
        ))}
      </div>

      <div className="btn-group">
        {isLeader ? (
          <button
            className="btn btn-primary"
            onClick={handleStartGame}
            disabled={starting || players.length < 2 || roomData.status === 'PLAYING'}
          >
            {roomData.status === 'PLAYING'
              ? '🎮 กำลังเล่นเกมอยู่'
              : starting
              ? 'กำลังเริ่มเกม...'
              : players.length < 2
              ? '⚠️ ต้องการผู้เล่นอย่างน้อย 2 คน'
              : '🚀 เริ่มเกมทันที'}
          </button>
        ) : (
          <div style={{ color: '#a0a0be', fontSize: '0.9rem', marginBottom: 6 }}>
            ⏳ กำลังรอหัวหน้าห้องกดเริ่มเกม...
          </div>
        )}

        <button className="btn btn-outline" onClick={handleLeave}>
          🚪 ออกจากห้อง
        </button>
      </div>
    </div>
  );
}
