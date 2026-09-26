import { motion } from 'framer-motion';

export const getCardStyle = (type: string, name?: string) => {
    switch(type) {
        case 'DEFUSE': return 'bg-green-500 text-white';
        case 'BOMB': return 'bg-neutral-900 text-red-500 border-red-500';
        case 'ATTACK': return 'bg-orange-500 text-white';
        case 'SKIP': return 'bg-blue-500 text-white';
        case 'FAVOR': return 'bg-purple-500 text-white';
        case 'NOPE': return 'bg-red-600 text-white';
        case 'SEETHEFUTURE': return 'bg-pink-500 text-white';
        case 'SHUFFLE': return 'bg-amber-700 text-white';
        default: 
            if (name === 'Cattermelon') return 'bg-lime-200 text-green-900';
            if (name === 'Beard Cat') return 'bg-stone-300 text-stone-900';
            if (name === 'Tacocat') return 'bg-yellow-200 text-orange-900';
            if (name === 'Hairy Potato Cat') return 'bg-amber-200 text-amber-900';
            if (name === 'Rainbow-Ralphing Cat') return 'bg-cyan-200 text-purple-900';
            return 'bg-gray-100 text-black'; // NORMAL fallback
    }
}

export const getCardIcon = (type: string, name?: string) => {
    switch(type) {
        case 'DEFUSE': return '🔧';
        case 'BOMB': return '💣';
        case 'ATTACK': return '⚔️';
        case 'SKIP': return '⏭️';
        case 'FAVOR': return '🤲';
        case 'NOPE': return '🚫';
        case 'SEETHEFUTURE': return '👁️';
        case 'SHUFFLE': return '🔀';
        default: 
            if (name === 'Cattermelon') return '🍉';
            if (name === 'Beard Cat') return '🧔';
            if (name === 'Tacocat') return '🌮';
            if (name === 'Hairy Potato Cat') return '🥔';
            if (name === 'Rainbow-Ralphing Cat') return '🌈';
            return '🐈'; // NORMAL fallback
    }
}

interface CardProps {
    card: { id: number, type: string, name: string };
    style?: React.CSSProperties;
    className?: string;
    onClick?: () => void;
}

export default function CardUI({ card, style, className, onClick }: CardProps) {
    const colorClass = getCardStyle(card.type, card.name);
    const icon = getCardIcon(card.type, card.name);
    
    return (
        <motion.div 
            className={`relative w-24 h-36 ${colorClass} rounded-xl border-4 border-white shadow-[0_5px_15px_rgba(0,0,0,0.3)] flex flex-col p-2 cursor-pointer origin-bottom ${className}`}
            style={style}
            whileHover={{ y: -30, scale: 1.15, zIndex: 50 }}
            onClick={onClick}
        >
            <div className="absolute top-1 left-1 text-xs font-bold drop-shadow-md">{icon}</div>
            <div className="flex-1 flex flex-col items-center justify-center space-y-1">
                <span className="text-3xl drop-shadow-lg">{icon}</span>
                <span className="text-[9px] font-black uppercase text-center leading-tight drop-shadow-md break-words w-full">{card.name}</span>
            </div>
            <div className="absolute bottom-1 right-1 text-xs font-bold drop-shadow-md rotate-180">{icon}</div>
        </motion.div>
    )
}
