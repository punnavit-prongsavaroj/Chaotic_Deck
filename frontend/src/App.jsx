import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { User, Users, Play, Download } from 'lucide-react';

const api = axios.create({
  baseURL: '/api'
});

export default function App() {
  const [player, setPlayer] = useState(null);
  const [playerName, setPlayerName] = useState('');
  const [roomId, setRoomId] = useState('');
  const [gameState, setGameState] = useState('LOGIN'); // LOGIN, LOBBY, GAME
  const [playersInRoom, setPlayersInRoom] = useState([]);
  const [messages, setMessages] = useState([]);

  // Mock game state
  const [hand, setHand] = useState(['Attack', 'Skip', 'Defuse', 'See the Future', 'Nope']);
  const [turn, setTurn] = useState(false);

  const addMessage = (msg) => {
    setMessages(prev => [...prev, msg]);
  };

  const handleCreatePlayer = async (e) => {
    e.preventDefault();
    if (!playerName) return;
    try {
      const res = await api.post('/players', { name: playerName });
      setPlayer(res.data);
      setGameState('LOBBY');
      addMessage(`Welcome, ${res.data.name}!`);
    } catch (err) {
      console.error(err);
      addMessage('Error creating player');
    }
  };

  const handleCreateRoom = async () => {
    if (!roomId) return;
    try {
      await api.post(`/rooms?roomId=${roomId}&leaderId=${player.id}`);
      addMessage(`Room ${roomId} created.`);
      await handleJoinRoom(roomId);
    } catch (err) {
      console.error(err);
      addMessage('Error creating room');
    }
  };

  const handleJoinRoom = async (joinRoomId = roomId) => {
    if (!joinRoomId) return;
    try {
      await api.post(`/rooms/${joinRoomId}/join?playerId=${player.id}`);
      setRoomId(joinRoomId);
      setGameState('GAME');
      addMessage(`Joined room ${joinRoomId}`);
      fetchPlayers();
    } catch (err) {
      console.error(err);
      addMessage(`Error joining room: ${err.response?.data || err.message}`);
    }
  };

  const fetchPlayers = async () => {
    try {
      const res = await api.get(`/rooms/${roomId}/players`);
      setPlayersInRoom(res.data);
    } catch (err) {
      console.error(err);
    }
  };

  const handleStartGame = async () => {
    try {
      await api.post(`/game/${roomId}/start?playerCount=${playersInRoom.length}`);
      addMessage('Game started!');
      setTurn(true);
    } catch (err) {
      console.error(err);
    }
  };

  const handleDrawCard = async () => {
    try {
      const res = await api.post(`/game/${roomId}/draw?playerId=${player.id}`);
      addMessage(`Draw result: ${res.data}`);
      setHand([...hand, 'Unknown Card']); // mock drawing card
    } catch (err) {
      console.error(err);
      addMessage('Error drawing card');
    }
  };

  const handlePlayCard = async (cardType, index) => {
    try {
      const res = await api.post(`/game/${roomId}/play?playerId=${player.id}&cardType=${cardType}`);
      addMessage(res.data);
      const newHand = [...hand];
      newHand.splice(index, 1);
      setHand(newHand);
    } catch (err) {
      console.error(err);
    }
  };

  if (gameState === 'LOGIN') {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-900 text-white">
        <form onSubmit={handleCreatePlayer} className="bg-gray-800 p-8 rounded-xl shadow-2xl flex flex-col items-center gap-4 border border-gray-700">
          <h1 className="text-3xl font-bold mb-4 text-red-500">Chaotic Deck</h1>
          <User size={48} className="text-gray-400" />
          <input 
            type="text" 
            placeholder="Enter your name"
            className="px-4 py-2 rounded bg-gray-700 border border-gray-600 focus:outline-none focus:border-red-500 text-center text-lg w-64"
            value={playerName}
            onChange={(e) => setPlayerName(e.target.value)}
          />
          <button type="submit" className="w-full bg-red-600 hover:bg-red-700 font-bold py-2 px-4 rounded transition-colors">
            Enter Game
          </button>
        </form>
      </div>
    );
  }

  if (gameState === 'LOBBY') {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-900 text-white">
        <div className="bg-gray-800 p-8 rounded-xl shadow-2xl flex flex-col gap-6 border border-gray-700 w-96">
          <h2 className="text-2xl font-bold text-center text-red-400">Lobby</h2>
          <div className="text-center text-gray-400 mb-2">Welcome, {player.name}</div>
          
          <div className="flex flex-col gap-2">
            <input 
              type="text" 
              placeholder="Room ID (e.g. ROOM123)"
              className="px-4 py-2 rounded bg-gray-700 border border-gray-600 focus:outline-none focus:border-red-500"
              value={roomId}
              onChange={(e) => setRoomId(e.target.value)}
            />
            <button onClick={handleCreateRoom} className="bg-red-600 hover:bg-red-700 font-bold py-2 rounded flex items-center justify-center gap-2">
              <Users size={18} /> Create Room
            </button>
            <div className="text-center text-gray-500 text-sm">OR</div>
            <button onClick={() => handleJoinRoom(roomId)} className="bg-gray-600 hover:bg-gray-500 font-bold py-2 rounded">
              Join Room
            </button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen relative overflow-hidden flex flex-col">
      {/* Top Bar info */}
      <div className="absolute top-4 left-4 z-10 flex gap-4">
        <div className="bg-black bg-opacity-50 px-4 py-2 rounded-full border border-gray-600 flex items-center gap-2 shadow-lg">
          <div className="w-8 h-8 bg-blue-500 rounded-full flex items-center justify-center font-bold">
            {player.name.charAt(0)}
          </div>
          <span>{player.name}</span>
        </div>
        <div className="bg-black bg-opacity-50 px-4 py-2 rounded-full border border-gray-600 shadow-lg">
          Room: <span className="text-red-400 font-bold">{roomId}</span>
        </div>
        <button onClick={handleStartGame} className="bg-green-600 hover:bg-green-500 px-4 py-2 rounded-full font-bold flex items-center gap-2 shadow-lg">
          <Play size={16} /> Start
        </button>
      </div>

      {/* Other Players (Mock layout) */}
      <div className="absolute top-8 left-1/2 -translate-x-1/2 flex flex-col items-center gap-2">
        <div className="w-16 h-16 bg-purple-600 border-4 border-green-500 rounded-full shadow-[0_0_15px_rgba(34,197,94,0.6)] flex items-center justify-center text-2xl font-bold">
          P2
        </div>
        <div className="bg-black bg-opacity-60 px-3 py-1 rounded-full text-sm">Player 2</div>
        <div className="flex -space-x-4 mt-1">
          {[1,2,3,4].map(i => (
            <div key={i} className="w-10 h-14 bg-blue-900 border-2 border-white rounded shadow-md"></div>
          ))}
        </div>
      </div>

      <div className="absolute top-1/2 left-8 -translate-y-1/2 flex flex-col items-center gap-2">
        <div className="w-16 h-16 bg-red-600 border-4 border-gray-700 rounded-full flex items-center justify-center text-2xl font-bold">
          P3
        </div>
        <div className="bg-black bg-opacity-60 px-3 py-1 rounded-full text-sm">Player 3</div>
        <div className="flex flex-col -space-y-8 mt-1">
          {[1,2,3].map(i => (
            <div key={i} className="w-14 h-10 bg-blue-900 border-2 border-white rounded shadow-md rotate-90"></div>
          ))}
        </div>
      </div>

      <div className="absolute top-1/2 right-8 -translate-y-1/2 flex flex-col items-center gap-2">
        <div className="w-16 h-16 bg-yellow-600 border-4 border-gray-700 rounded-full flex items-center justify-center text-2xl font-bold text-black">
          P4
        </div>
        <div className="bg-black bg-opacity-60 px-3 py-1 rounded-full text-sm">Player 4</div>
        <div className="flex flex-col -space-y-8 mt-1">
          {[1,2,3,4,5].map(i => (
            <div key={i} className="w-14 h-10 bg-blue-900 border-2 border-white rounded shadow-md -rotate-90"></div>
          ))}
        </div>
      </div>

      {/* Center Table */}
      <div className="flex-1 flex items-center justify-center">
        <div className="relative w-64 h-64 border-4 border-blue-500 border-opacity-50 rounded-full flex items-center justify-center shadow-[0_0_50px_rgba(59,130,246,0.3)]">
          {/* Deck Pile */}
          <button 
            onClick={handleDrawCard}
            className="absolute left-8 w-24 h-36 bg-blue-900 rounded-xl border-4 border-white shadow-2xl transform -rotate-12 hover:-translate-y-2 transition-transform flex flex-col items-center justify-center gap-2 group"
          >
             <div className="w-16 h-16 rounded-full border-4 border-white flex items-center justify-center bg-red-500 text-white font-bold text-2xl">
                C
             </div>
             <div className="absolute inset-0 bg-black bg-opacity-0 group-hover:bg-opacity-20 rounded-lg transition-colors flex items-center justify-center">
                <Download size={32} className="text-white opacity-0 group-hover:opacity-100" />
             </div>
          </button>
          
          {/* Discard Pile */}
          <div className="absolute right-8 w-24 h-36 bg-gray-100 rounded-xl border-4 border-gray-300 shadow-xl transform rotate-6 flex flex-col items-center justify-center text-gray-800 font-bold p-2 text-center overflow-hidden">
             <div className="absolute top-2 left-2 text-xs">Nope</div>
             <div className="absolute bottom-2 right-2 text-xs rotate-180">Nope</div>
             <div className="text-xl">NOPE</div>
          </div>
        </div>
      </div>

      {/* Bottom Player Area (Hand) */}
      <div className="absolute bottom-0 w-full flex justify-center pb-8 pt-20 bg-gradient-to-t from-black via-transparent to-transparent">
        <div className="flex -space-x-8 hover:space-x-2 transition-all duration-300 items-end">
          {hand.map((card, i) => (
            <button 
              key={i} 
              onClick={() => handlePlayCard(card, i)}
              className="w-32 h-48 bg-white rounded-xl border-4 border-gray-200 shadow-[0_10px_20px_rgba(0,0,0,0.5)] flex flex-col items-center justify-center transform hover:-translate-y-8 hover:z-20 transition-all text-gray-900 relative"
              style={{
                transformOrigin: 'bottom center',
                rotate: `${(i - (hand.length-1)/2) * 5}deg`,
                zIndex: i
              }}
            >
              <div className="absolute top-2 left-2 font-bold text-sm">{card.charAt(0)}</div>
              <div className="text-center font-bold px-2">{card}</div>
            </button>
          ))}
        </div>
      </div>

      {/* Game Logs / Messages */}
      <div className="absolute bottom-4 left-4 w-64 max-h-48 overflow-y-auto bg-black bg-opacity-70 rounded p-2 text-sm">
        {messages.map((m, i) => <div key={i} className="mb-1">{m}</div>)}
      </div>

    </div>
  );
}
