const fs = require('fs');
let code = fs.readFileSync('src/components/GameBoard.tsx', 'utf8');
code = code.replace(/const handlePlaySelected = async \(\) => \{[\s\S]*?const toggleSelect = \(cardId: number\) => \{[\s\S]*?\}\n/g, 
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
    const uniqueId = \\-\\;
    setSelectedCards(prev => 
      prev.includes(uniqueId) ? prev.filter(c => c !== uniqueId) : [...prev, uniqueId]
    )
  }
);
fs.writeFileSync('src/components/GameBoard.tsx', code);
