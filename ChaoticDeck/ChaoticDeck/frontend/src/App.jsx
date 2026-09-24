import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import NamePage from './pages/NamePage';
import LobbyPage from './pages/LobbyPage';
import JoinPage from './pages/JoinPage';
import RoomPage from './pages/RoomPage';

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<NamePage />} />
        <Route path="/lobby" element={<LobbyPage />} />
        <Route path="/join" element={<JoinPage />} />
        <Route path="/room/:roomId" element={<RoomPage />} />
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </BrowserRouter>
  );
}
