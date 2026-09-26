import { useState, useEffect } from 'react'
import GameBoard from './components/GameBoard'
import { createPlayer, createRoom, joinRoom, getRoomDetails, startGame, leaveRoom } from './api'
import { Client } from '@stomp/stompjs'
import SockJS from 'sockjs-client'

export default function App() {
  const [player, setPlayer] = useState<{id: number, name: string} | null>(null)
  const [room, setRoom] = useState<any>(null)
  const [nameInput, setNameInput] = useState('')
  const [roomInput, setRoomInput] = useState('')
  const [stompClient, setStompClient] = useState<Client | null>(null)
  const [isStarting, setIsStarting] = useState(false)

  useEffect(() => {
    const savedPlayer = localStorage.getItem('player');
    const savedRoomId = localStorage.getItem('roomId');
    if (savedPlayer) {
      try {
        const p = JSON.parse(savedPlayer);
        setPlayer(p);
        if (savedRoomId) {
          getRoomDetails(savedRoomId).then(r => {
            setRoom(r);
            connectWebSocket(savedRoomId);
          }).catch(err => {
            console.error("Failed to restore room:", err);
            localStorage.removeItem('roomId');
          });
        }
      } catch (e) {
        localStorage.removeItem('player');
      }
    }
  }, []);

  const handleLogout = () => {
    if (stompClient) stompClient.deactivate();
    setPlayer(null);
    setRoom(null);
    localStorage.removeItem('player');
    localStorage.removeItem('roomId');
  }

  const handleLeaveRoom = async () => {
    if (!room || !player) return;
    try {
      const roomIdStr = room.RoomID || room.roomID || room.roomId;
      if (roomIdStr) {
        await leaveRoom(roomIdStr, player.id);
      }
    } catch (err) {
      console.error("Failed to notify server to leave room", err);
    }
    if (stompClient) stompClient.deactivate();
    setRoom(null);
    localStorage.removeItem('roomId');
  }

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!nameInput.trim()) return
    try {
      const p = await createPlayer(nameInput)
      setPlayer(p)
      localStorage.setItem('player', JSON.stringify(p))
    } catch (err) {
      alert("Login failed")
    }
  }

  const handleCreateRoom = async () => {
    if (!player) return
    try {
      const r = await createRoom(player.id)
      setRoom(r)
      const rId = r.RoomID || r.roomID || r.roomId;
      localStorage.setItem('roomId', rId)
      connectWebSocket(rId)
    } catch (err) {
      alert("Failed to create room")
    }
  }

  const handleJoinRoom = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!roomInput.trim() || !player) return
    try {
      await joinRoom(roomInput.toUpperCase(), player.id)
      const r = await getRoomDetails(roomInput.toUpperCase())
      setRoom(r)
      const rId = r.RoomID || r.roomID || r.roomId;
      localStorage.setItem('roomId', rId)
      connectWebSocket(rId)
    } catch (err) {
      alert("Failed to join room. It might be full or started.")
    }
  }

  const connectWebSocket = (roomId: string) => {
    const client = new Client({
      webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
      onConnect: () => {
        client.subscribe(`/topic/room/${roomId}`, (msg) => {
          const body = msg.body
          if (body === 'GAME_STARTED') {
            setRoom((prev: any) => ({ ...prev, status: 'PLAYING' }))
          } else if (body.startsWith('PLAYER_JOINED') || body.startsWith('PLAYER_LEFT')) {
            getRoomDetails(roomId).then(r => setRoom(r))
          }
          window.dispatchEvent(new CustomEvent('ROOM_WS_EVENT', { detail: msg.body }))
        })
      }
    })
    client.activate()
    setStompClient(client)
  }

  const handleStartGame = async () => {
    if (!room || !player || isStarting) return
    setIsStarting(true)
    try {
      await startGame(room.RoomID || room.roomId || room.roomID, player.id)
    } catch (err) {
      alert("Failed to start game")
      setIsStarting(false)
    }
  }

  // 1. Login Screen
  if (!player) {
    return (
      <div className="flex flex-col items-center justify-center w-full h-full bg-neutral-900 text-white">
        <h1 className="text-5xl font-black text-red-500 mb-8 drop-shadow-[0_0_15px_rgba(239,68,68,0.8)]">CHAOTIC DECK</h1>
        <form onSubmit={handleLogin} className="flex flex-col items-center space-y-4 bg-black/40 p-8 rounded-2xl border border-red-900 shadow-2xl">
          <input 
            type="text" 
            placeholder="Enter your name..." 
            value={nameInput}
            onChange={(e) => setNameInput(e.target.value)}
            className="px-4 py-3 bg-neutral-800 rounded-lg outline-none focus:ring-2 focus:ring-red-500 border border-neutral-700 w-64 text-center text-lg font-bold"
          />
          <button type="submit" className="w-full py-3 bg-red-600 hover:bg-red-500 font-bold rounded-lg transition-colors shadow-lg">PLAY</button>
        </form>
      </div>
    )
  }

  // 2. Lobby Menu
  if (!room) {
    return (
      <div className="flex flex-col items-center justify-center w-full h-full bg-neutral-900 text-white">
        <div className="flex items-center gap-4 mb-8">
          <h2 className="text-3xl font-bold">Welcome, <span className="text-yellow-400">{player.name}</span>!</h2>
          <button onClick={handleLogout} className="px-3 py-1 bg-neutral-700 hover:bg-neutral-600 rounded text-sm font-bold transition-colors">Logout</button>
        </div>
        
        <div className="flex flex-col md:flex-row gap-8 w-full max-w-2xl px-8">
          {/* Create Room */}
          <div className="flex-1 bg-black/40 p-8 rounded-2xl border border-red-900 shadow-2xl flex flex-col items-center justify-center space-y-4">
            <h3 className="text-xl font-bold">Host a Game</h3>
            <p className="text-sm text-gray-400 text-center mb-4">Create a new room and invite your friends to explode together.</p>
            <button onClick={handleCreateRoom} className="w-full py-3 bg-red-600 hover:bg-red-500 font-bold rounded-lg transition-colors shadow-lg">CREATE ROOM</button>
          </div>

          {/* Join Room */}
          <div className="flex-1 bg-black/40 p-8 rounded-2xl border border-blue-900 shadow-2xl flex flex-col items-center justify-center space-y-4">
            <h3 className="text-xl font-bold text-blue-400">Join a Game</h3>
            <p className="text-sm text-gray-400 text-center mb-4">Enter a room code to join an existing game.</p>
            <form onSubmit={handleJoinRoom} className="w-full flex flex-col space-y-2">
              <input 
                type="text" 
                placeholder="Room Code" 
                value={roomInput}
                onChange={(e) => setRoomInput(e.target.value)}
                className="px-4 py-3 bg-neutral-800 rounded-lg outline-none focus:ring-2 focus:ring-blue-500 border border-neutral-700 w-full text-center text-lg font-bold uppercase"
              />
              <button type="submit" className="w-full py-3 bg-blue-600 hover:bg-blue-500 font-bold rounded-lg transition-colors shadow-lg">JOIN</button>
            </form>
          </div>
        </div>
      </div>
    )
  }

  // 3. Waiting Room
  if (room.status === 'WAITING') {
    const isLeader = room.leaderId === player.id || room.ledder_id === player.id;
    const players = room.players || [{ name: player.name, isLeader: true }];
    const roomIdStr = room.RoomID || room.roomId || room.roomID;

    return (
      <div className="flex flex-col items-center justify-center w-full h-full bg-neutral-900 text-white">
        <div className="bg-black/40 p-8 rounded-2xl border border-red-900 shadow-2xl w-full max-w-md text-center flex flex-col items-center">
          <h2 className="text-2xl font-bold text-gray-400 mb-2">Room Code</h2>
          <div className="text-5xl font-black text-white tracking-widest bg-neutral-800 px-6 py-3 rounded-lg mb-8 border border-neutral-700 shadow-inner">
            {roomIdStr}
          </div>
          
          <h3 className="text-lg font-bold mb-4">Players ({players.length}/{room.maxPlayers || 4})</h3>
          <ul className="w-full space-y-2 mb-8">
            {players.map((p: any, i: number) => (
               <li key={i} className="bg-neutral-800 py-2 rounded-lg font-bold text-yellow-400 flex items-center justify-center space-x-2">
                 {p.isLeader && <span>👑</span>}<span>{p.name}</span>
               </li>
            ))}
            {players.length < (room.maxPlayers || 4) && (
                <li className="bg-neutral-800/50 py-2 rounded-lg font-bold text-gray-500 animate-pulse border border-dashed border-gray-700">
                Waiting for players...
                </li>
            )}
          </ul>

          {isLeader ? (
            <button 
              disabled={isStarting}
              onClick={handleStartGame}
              className={`w-full py-4 font-black text-xl rounded-lg transition-colors shadow-[0_0_15px_rgba(22,163,74,0.5)] mb-4 ${isStarting ? 'bg-gray-600 text-gray-400 cursor-not-allowed' : 'bg-green-600 hover:bg-green-500 text-white'}`}
            >
              {isStarting ? 'STARTING...' : 'START GAME'}
            </button>
          ) : (
             <div className="text-gray-400 font-bold animate-pulse mb-4">Waiting for leader to start...</div>
          )}

          <button onClick={handleLeaveRoom} className="text-gray-400 hover:text-white underline text-sm transition-colors mt-2">
            Leave Room
          </button>
        </div>
      </div>
    )
  }

  // 4. Game Board
  return (
    <div className="relative w-full h-full">
      <button 
        onClick={handleLeaveRoom} 
        className="absolute top-4 right-4 z-[999] bg-neutral-800 border border-neutral-600 hover:bg-red-600 text-white font-bold py-2 px-4 rounded shadow-lg transition-colors"
      >
        Leave Game
      </button>
      <GameBoard player={player} room={room} />
    </div>
  )
}
