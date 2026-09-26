import { useState, useEffect } from 'react'
import CardUI from './CardUI'
import { getGameState, getHand, getDiscardPile, drawCard, playCards, getSeeTheFuture, defuseBomb, giveFavor } from '../api'

export default function GameBoard({ player, room }: { player: any, room: any }) {
  const [gameState, setGameState] = useState<any>(null)
  const [hand, setHand] = useState<any[]>([])
  const [discardPile, setDiscardPile] = useState<any[]>([])
  const [selectedCards, setSelectedCards] = useState<string[]>([])
  const [isActionPending, setIsActionPending] = useState(false)
  const [futureCards, setFutureCards] = useState<any[]>([])
  const [pendingMessage, setPendingMessage] = useState<string | null>(null)
  const [targetSelection, setTargetSelection] = useState<{ active: boolean, actionCardType: string, selectedCardIds: number[], needsCardName?: boolean } | null>(null)
  const [targetCardNameInput, setTargetCardNameInput] = useState<string>('')
  const [defuseModalActive, setDefuseModalActive] = useState<boolean>(false)
  const [defusePosition, setDefusePosition] = useState<number>(0)
  const roomIdStr = room.RoomID || room.roomId || room.roomID;

  const fetchState = async () => {
    try {
      const state = await getGameState(roomIdStr)
      setGameState(state)
      const h = await getHand(roomIdStr, player.id)
      setHand(h)
      const d = await getDiscardPile(roomIdStr)
      setDiscardPile(d)
    } catch (err) {
      console.error("Failed to fetch game state", err)
    }
  }

  useEffect(() => {
    fetchState()
    
    const handleWsEvent = async (e: any) => {
      console.log("Custom WebSocket event:", e.detail)
      const msg = e.detail;
      if (msg && typeof msg === 'string') {
        if (msg.startsWith('SEETHEFUTURE_RESOLVED:')) {
          const pId = msg.split(':')[1];
          if (parseInt(pId) === player.id) {
             const top3 = await getSeeTheFuture(roomIdStr);
             setFutureCards(top3);
          }
        } else if (msg.startsWith('ACTION_PENDING:')) {
          const parts = msg.split(':');
          setPendingMessage(`Waiting for NOPE (5s)... (${parts[1]})`);
        } else if (msg.startsWith('NOPE_PLAYED:')) {
          setPendingMessage(`NOPE PLAYED! Waiting...`);
        } else if (msg.startsWith('ACTION_RESOLVED:') || msg.startsWith('ACTION_CANCELED:')) {
          setPendingMessage(null);
        }
      }
      fetchState()
    }
    window.addEventListener('ROOM_WS_EVENT', handleWsEvent)
    
    return () => {
      window.removeEventListener('ROOM_WS_EVENT', handleWsEvent)
    }
  }, [roomIdStr, player.id])

  const handleDraw = async () => {
    if (gameState?.currentTurnPlayerId !== player.id || isActionPending) return
    setIsActionPending(true)
    try {
      const res = await drawCard(roomIdStr, player.id)
      if (res && res !== "Safe! You drew a normal card." && !res.startsWith("BOOM!")) {
        alert(res);
      }
    } catch (err) {
      alert("Failed to draw card")
    } finally {
      setTimeout(() => setIsActionPending(false), 500)
    }
  }

    const handlePlaySelected = async () => {
    if (selectedCards.length === 0 || isActionPending) return
    const firstSelected = selectedCards[0]
    const cardIdStr = firstSelected.split('-')[0]
    const cardId = parseInt(cardIdStr)
    const mockCard = getMockCard(cardId)
    if (!mockCard) return
    
    // Determine actual card type based on combo length
    const actualCardType = selectedCards.length === 2 ? 'COMBO2' : selectedCards.length === 3 ? 'COMBO3' : selectedCards.length === 5 ? 'COMBO5' : mockCard.type;

    const parsedSelectedCards = selectedCards.map(s => parseInt(s.split('-')[0]))

    if (actualCardType === 'FAVOR' || actualCardType === 'COMBO2' || actualCardType === 'COMBO3') {
       setTargetSelection({
         active: true,
         actionCardType: actualCardType,
         selectedCardIds: parsedSelectedCards,
         needsCardName: actualCardType === 'COMBO3'
       });
       return;
    }

    setIsActionPending(true)
    try {
      if (actualCardType === 'DEFUSE') {
         await playCards(roomIdStr, player.id, parsedSelectedCards, actualCardType)
         setDefuseModalActive(true)
      } else {
         await playCards(roomIdStr, player.id, parsedSelectedCards, actualCardType)
      }
      setSelectedCards([])
    } catch (err) {
      alert("Failed to play cards")
    } finally {
      setTimeout(() => setIsActionPending(false), 500)
    }
  }

  const executeDefuse = async () => {
    setIsActionPending(true);
    setDefuseModalActive(false);
    try {
      await defuseBomb(roomIdStr, player.id, defusePosition);
      setDefusePosition(0);
    } catch (err) {
      alert("Failed to defuse bomb");
    } finally {
      setTimeout(() => setIsActionPending(false), 500);
    }
  }

  const executeTargetedPlay = async (targetPlayerId: number) => {
    if (!targetSelection) return;
    setIsActionPending(true);
    try {
      await playCards(roomIdStr, player.id, targetSelection.selectedCardIds, targetSelection.actionCardType, targetPlayerId, undefined, targetSelection.needsCardName ? targetCardNameInput : undefined);
      setTargetSelection(null);
      setSelectedCards([]);
      setTargetCardNameInput('');
    } catch (err) {
      alert("Failed to play cards");
    } finally {
      setTimeout(() => setIsActionPending(false), 500);
    }
  }

  const toggleSelect = (cardId: number, index: number) => {
    const uniqueId = `${cardId}-${index}`;
    setSelectedCards(prev => 
      prev.includes(uniqueId) ? prev.filter(c => c !== uniqueId) : [...prev, uniqueId]
    )
  }

  const getMockCard = (cardId: number) => {
    if (cardId === 1) return { id: 1, type: 'BOMB', name: 'Exploding Kitten' }
    if (cardId === 2) return { id: 2, type: 'DEFUSE', name: 'Defuse' }
    if (cardId === 3) return { id: 3, type: 'SKIP', name: 'Skip' }
    if (cardId === 4) return { id: 4, type: 'ATTACK', name: 'Attack' }
    if (cardId === 5) return { id: 5, type: 'SHUFFLE', name: 'Shuffle' }
    if (cardId === 6) return { id: 6, type: 'SEETHEFUTURE', name: 'See The Future' }
    if (cardId === 7) return { id: 7, type: 'NORMAL', name: 'Cattermelon' }
    if (cardId === 8) return { id: 8, type: 'NORMAL', name: 'Beard Cat' }
    if (cardId === 9) return { id: 9, type: 'NORMAL', name: 'Tacocat' }
    if (cardId === 10) return { id: 10, type: 'NORMAL', name: 'Hairy Potato Cat' }
    if (cardId === 11) return { id: 11, type: 'NORMAL', name: 'Rainbow-Ralphing Cat' }
    if (cardId === 12) return { id: 12, type: 'FAVOR', name: 'Favor' }
    return { id: cardId, type: 'NORMAL', name: 'Unknown' }
  }

  if (!gameState) return <div className="text-white text-center p-8 flex items-center justify-center h-full w-full bg-neutral-900"><div className="animate-pulse font-bold text-2xl text-red-500">Loading Game...</div></div>

  const isMyTurn = gameState.currentTurnPlayerId === player.id
  const opponents = (gameState.allPlayers || []).filter((pid: number) => pid !== player.id)

  const giveFavorRequest = gameState?.playerStatuses?.[player.id]?.startsWith('PENDING_FAVOR:') ? parseInt(gameState.playerStatuses[player.id].split(':')[1]) : null;
  const waitingForFavorFrom = opponents.find((opId: number) => gameState?.playerStatuses?.[opId] === `PENDING_FAVOR:${player.id}`);

  const handleGiveFavor = async (cardId: number) => {
    if (!giveFavorRequest) return;
    try {
       await giveFavor(roomIdStr, player.id, cardId);
       fetchState(); // Force update instantly
    } catch (err) {
       console.error(err);
       alert("Failed to give favor");
    }
  }

  return (
    <div className="relative w-full h-full flex flex-col items-center justify-between overflow-hidden bg-neutral-900 pb-8 pt-16">
      
      {/* Give Favor Modal */}
      {giveFavorRequest && (
        <div className="absolute inset-0 z-[1000] bg-black/90 flex flex-col items-center justify-center backdrop-blur-sm">
           <h2 className="text-4xl font-black text-yellow-500 mb-8 drop-shadow-[0_0_15px_rgba(234,179,8,0.8)] animate-bounce">YOU MUST GIVE A FAVOR!</h2>
           <p className="text-xl text-white mb-8 font-bold">Player {gameState?.playerNames?.[giveFavorRequest] || giveFavorRequest} has demanded a card from you.</p>
           <p className="text-md text-gray-400 mb-12">Click a card below to hand it over.</p>
           
           <div className="flex flex-wrap justify-center max-w-4xl gap-4">
             {hand.filter(h => h.amount > 0).map((h) => (
               <div 
                 key={h.card.id}
                 onClick={() => handleGiveFavor(h.card.id)}
                 className="cursor-pointer transition-transform hover:scale-110 hover:-translate-y-4"
               >
                 <CardUI card={getMockCard(h.card.id)} />
               </div>
             ))}
           </div>
        </div>
      )}
      
      {/* Future Cards Modal */}
      {futureCards.length > 0 && (
        <div className="absolute inset-0 z-[1000] bg-black/80 flex flex-col items-center justify-center backdrop-blur-sm">
           <h2 className="text-4xl font-black text-pink-500 mb-8 drop-shadow-[0_0_15px_rgba(236,72,153,0.8)]">THE FUTURE IS REVEALED</h2>
           <div className="flex space-x-8">
             {futureCards.map((f, i) => (
               <div key={i} className="flex flex-col items-center space-y-4">
                 <span className="text-white font-bold bg-neutral-800 px-4 py-1 rounded-full border border-neutral-600 shadow-lg text-sm">
                   {i === 0 ? 'TOP CARD' : i === 1 ? '2ND CARD' : '3RD CARD'}
                 </span>
                 <CardUI card={getMockCard(f.top3Count)} className="scale-125 !w-32 !h-48 shadow-[0_0_30px_rgba(236,72,153,0.4)]" />
               </div>
             ))}
           </div>
           <button onClick={() => setFutureCards([])} className="mt-16 px-12 py-4 bg-pink-600 hover:bg-pink-500 text-white font-black text-xl rounded-full shadow-[0_0_20px_rgba(236,72,153,0.8)] transition-transform hover:scale-110">
             ACKNOWLEDGE
           </button>
        </div>
      )}

      {/* Target Selection Modal */}
      {targetSelection?.active && (
        <div className="absolute inset-0 z-[1000] bg-black/80 flex flex-col items-center justify-center backdrop-blur-sm">
           <h2 className="text-4xl font-black text-purple-500 mb-8 drop-shadow-[0_0_15px_rgba(168,85,247,0.8)]">SELECT TARGET</h2>
           
           {targetSelection.needsCardName && (
             <div className="mb-8 w-1/3">
               <label className="text-white font-bold mb-2 block text-center">Which card do you want to steal?</label>
               <input type="text" value={targetCardNameInput} onChange={(e) => setTargetCardNameInput(e.target.value)} className="w-full p-4 rounded-lg text-black font-bold text-center" placeholder="e.g. DEFUSE, Tacocat" />
             </div>
           )}

           <div className="flex space-x-8">
             {opponents.map((opId: number) => {
               const opName = gameState?.playerNames?.[opId] || `Player ${opId}`;
               return (
                 <button 
                   key={opId}
                   onClick={() => executeTargetedPlay(opId)}
                   className="px-8 py-4 bg-purple-600 hover:bg-purple-500 text-white font-black text-xl rounded-2xl shadow-[0_0_20px_rgba(168,85,247,0.8)] transition-transform hover:scale-110"
                 >
                   {opName}
                 </button>
               );
             })}
           </div>
           <button onClick={() => setTargetSelection(null)} className="mt-12 text-gray-400 font-bold hover:text-white transition-colors underline">
             CANCEL
           </button>
        </div>
      )}

      {/* Defuse Position Modal */}
      {defuseModalActive && (
        <div className="absolute inset-0 z-[1000] bg-black/90 flex flex-col items-center justify-center backdrop-blur-sm">
           <h2 className="text-4xl font-black text-green-500 mb-8 drop-shadow-[0_0_15px_rgba(34,197,94,0.8)] animate-pulse">DEFUSE SUCCESSFUL!</h2>
           <p className="text-xl text-white mb-8 font-bold text-center max-w-lg">
             You saved yourself! Now, secretly put the Exploding Kitten back into the deck.
           </p>
           
           <div className="mb-8 w-full max-w-md bg-neutral-800 p-8 rounded-2xl border-2 border-neutral-600 shadow-2xl">
             <label className="text-white font-bold mb-4 block text-center text-xl">Select Position</label>
             <div className="flex flex-col space-y-4">
               <div className="flex justify-between items-center text-gray-300">
                 <span>0 = Top of Deck</span>
                 <span>(Next player draws it!)</span>
               </div>
               <div className="flex justify-between items-center text-gray-300">
                 <span>1 = Second from Top</span>
                 <span></span>
               </div>
               <div className="flex justify-between items-center text-gray-300">
                 <span>2 = Third from Top</span>
                 <span></span>
               </div>
               <input 
                 type="number" 
                 min="0"
                 max={gameState?.deckSize || 50}
                 value={defusePosition} 
                 onChange={(e) => setDefusePosition(parseInt(e.target.value) || 0)} 
                 className="w-full p-4 rounded-xl text-black font-black text-center text-2xl" 
               />
             </div>
           </div>

           <button 
             onClick={executeDefuse}
             className="px-12 py-4 bg-green-600 hover:bg-green-500 text-white font-black text-2xl rounded-2xl shadow-[0_0_20px_rgba(34,197,94,0.8)] transition-transform hover:scale-110"
           >
             PUT BOMB IN DECK
           </button>
        </div>
      )}

      {/* Top / Opponents Area */}
      <div className="w-full flex justify-center space-x-12 px-8">
        {opponents.map((opId: number) => (
          <div key={opId} className={`flex flex-col items-center transition-all ${gameState.currentTurnPlayerId === opId ? 'ring-4 ring-yellow-400 p-2 rounded-xl bg-yellow-900/20 scale-110' : ''}`}>
            <div className="w-16 h-16 bg-neutral-700 rounded-full flex items-center justify-center text-xl font-bold border-2 border-neutral-500 shadow-lg">
              {gameState.playerNames?.[opId]?.substring(0,2).toUpperCase() || 'OP'}
            </div>
            <div className="mt-2 text-sm font-bold text-gray-300">{gameState.playerNames?.[opId] || `Player ${opId}`}</div>
            <div className="text-xs text-gray-500">{gameState.playerHandSizes?.[opId] || 0} cards</div>
            <div className={`text-xs font-bold ${gameState.playerStatuses?.[opId] === 'DEAD' ? 'text-red-500 line-through' : 'text-green-400'}`}>{gameState.playerStatuses?.[opId]}</div>
          </div>
        ))}
      </div>

      {/* Center Table */}
      <div className="flex-1 flex items-center justify-center relative w-full my-8">
         
         {/* Pending Action Overlay */}
         {pendingMessage && (
           <div className="absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 z-[500] bg-red-600/90 text-white font-black px-8 py-4 rounded-full shadow-[0_0_30px_rgba(220,38,38,0.8)] border-4 border-white animate-pulse whitespace-nowrap">
              ⏳ {pendingMessage} ⏳
           </div>
         )}
         
         {/* Waiting for Favor Overlay */}
         {waitingForFavorFrom && !pendingMessage && (
           <div className="absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 z-[500] bg-purple-600/90 text-white font-black px-8 py-4 rounded-full shadow-[0_0_30px_rgba(168,85,247,0.8)] border-4 border-white animate-pulse whitespace-nowrap">
              ⏳ Waiting for Player {gameState?.playerNames?.[waitingForFavorFrom] || waitingForFavorFrom} to give you a card... ⏳
           </div>
         )}

         {/* Bomb Drawn Overlay */}
         {gameState?.playerStatuses?.[player.id] === 'PENDING_DEFUSE' && (
           <div className="absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 z-[600] flex flex-col items-center justify-center pointer-events-none w-full">
              <h2 className="text-5xl md:text-6xl font-black text-red-500 mb-8 drop-shadow-[0_0_20px_rgba(220,38,38,1)] animate-pulse">💣 BOOM! 💣</h2>
              <div className="scale-125 md:scale-150 mb-12 shadow-[0_0_50px_rgba(220,38,38,1)] animate-bounce">
                <CardUI card={getMockCard(1)} />
              </div>
              <div className="text-lg md:text-xl text-yellow-300 font-bold bg-black/80 px-8 py-4 rounded-full border-2 border-yellow-500 shadow-xl text-center backdrop-blur-sm pointer-events-auto">
                 You drew an Exploding Kitten!<br/>Select your DEFUSE card below and click PLAY.
              </div>
           </div>
         )}

         {/* Draw Pile */}
         <div className="absolute -translate-x-32 z-10">
            {isMyTurn && gameState?.requiredDraws > 1 && (
               <div className="absolute -top-12 left-1/2 transform -translate-x-1/2 bg-red-600 px-4 py-2 rounded-full border-2 border-white shadow-[0_0_15px_rgba(220,38,38,0.8)] animate-pulse z-50">
                  <span className="text-white font-black text-sm whitespace-nowrap">ATTACKED! DRAW {gameState.requiredDraws}</span>
               </div>
            )}
            <div onClick={handleDraw} className={`w-32 h-44 bg-neutral-800 rounded-xl shadow-[4px_4px_0_rgba(0,0,0,0.4)] border-4 ${isMyTurn && !isActionPending ? 'border-yellow-400 cursor-pointer hover:scale-105 hover:bg-neutral-700 animate-pulse' : 'border-red-900 opacity-80 cursor-not-allowed'} flex flex-col items-center justify-center transition-all relative z-10`}>
               <span className="text-red-500 font-black text-sm tracking-widest transform -rotate-45 mb-1">DRAW</span>
               <span className="text-white font-black text-4xl transform -rotate-45">{gameState.deckSize}</span>
            </div>
            <div className="w-32 h-44 bg-neutral-800 rounded-xl shadow-[2px_2px_0_rgba(0,0,0,0.4)] border-2 border-red-900 flex items-center justify-center absolute top-1 left-1 z-0"></div>
            <div className="w-32 h-44 bg-neutral-800 rounded-xl shadow-[2px_2px_0_rgba(0,0,0,0.4)] border-2 border-red-900 flex items-center justify-center absolute top-2 left-2 -z-10"></div>
         </div>
         
         {/* Discard Pile */}
         <div className="absolute translate-x-32 z-10">
            {discardPile.length > 0 ? (
              <div className="scale-90 shadow-2xl">
                <CardUI card={getMockCard(discardPile[0].cardId)} />
              </div>
            ) : (
              <div className="w-32 h-44 border-4 border-dashed border-neutral-700 rounded-xl flex items-center justify-center opacity-50 bg-black/20">
                <span className="text-neutral-500 font-bold transform -rotate-45">DISCARD</span>
              </div>
            )}
         </div>

         {/* Turn Indicator */}
         <div className="absolute -top-12 bg-black/80 px-8 py-3 rounded-full border border-neutral-700 text-xl font-bold shadow-2xl z-50">
            {isMyTurn ? <span className="text-yellow-400">🔥 YOUR TURN! (Draws left: {gameState.requiredDraws}) 🔥</span> : <span className="text-gray-400">{gameState.playerNames?.[gameState.currentTurnPlayerId]}'s Turn</span>}
         </div>
      </div>

      {/* Action Bar (Play Selected) */}
      <div className="h-16 w-full flex justify-center items-center z-50">
        {selectedCards.length > 0 && isMyTurn && (
          <button 
            disabled={isActionPending}
            onClick={handlePlaySelected} 
            className={`px-8 py-3 rounded-full font-black text-white transition-all transform border-2 ${isActionPending ? 'bg-gray-600 border-gray-400 opacity-50 cursor-not-allowed' : 'bg-blue-600 hover:bg-blue-500 shadow-[0_0_20px_rgba(37,99,235,0.8)] hover:scale-110 border-blue-400'}`}
          >
            {isActionPending ? 'PLAYING...' : 'PLAY SELECTED CARD(S)'}
          </button>
        )}
      </div>

      {/* My Hand */}
      <div className="relative w-full flex justify-center h-48 mt-4 group">
         <div className="absolute bottom-[-10px] flex justify-center w-full transition-transform duration-300">
            {hand.flatMap((h, i) => {
              const count = h.amount;
              const cards = [];
              for (let j=0; j<count; j++) {
                const isSelected = selectedCards.includes(`${h.card.id}-${j}`);
                cards.push(
                  <div 
                    key={`${h.card.id}-${j}`}
                    onClick={() => toggleSelect(h.card.id, j)}
                    className={`transition-all duration-300 cursor-pointer origin-bottom hover:!rotate-0 hover:!-translate-y-8 hover:z-[999] hover:scale-110 ${isSelected ? '!-translate-y-12 !rotate-0 scale-105 shadow-[0_0_30px_rgba(250,204,21,0.8)] z-[900]' : ''}`}
                    style={{
                      marginLeft: i === 0 && j === 0 ? '0' : '-50px',
                    }}
                  >
                    <CardUI card={getMockCard(h.card.id)} />
                  </div>
                )
              }
              return cards;
            })}
         </div>
      </div>
    </div>
  )
}



