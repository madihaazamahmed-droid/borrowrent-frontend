import { Link } from "react-router-dom";
import { Heart, MapPin } from "lucide-react";
import { Badge } from "./ui";
import { FALLBACK_IMG, label, money } from "../utils";

export default function ItemCard({ item, wished, onToggleWish }) {
  return (
    <div className="card group relative overflow-hidden transition hover:-translate-y-1 hover:shadow-xl">
      <Link to={`/items/${item.id}`} className="relative block aspect-[4/3] overflow-hidden bg-slate-100">
        <img src={item.imageUrl || FALLBACK_IMG} alt={item.name} onError={(e) => (e.currentTarget.src = FALLBACK_IMG)}
          className="h-full w-full object-cover transition duration-500 group-hover:scale-105" />
        <div className="absolute left-3 top-3"><Badge value={item.status} /></div>
      </Link>
      {onToggleWish && (
        <button onClick={() => onToggleWish(item.id)} aria-label="Toggle wishlist"
          className="absolute right-3 top-3 rounded-full bg-white/90 p-2 shadow hover:scale-110 transition">
          <Heart className={`h-4 w-4 ${wished ? "fill-red-500 text-red-500" : "text-slate-600"}`} />
        </button>
      )}
      <div className="p-5">
        <p className="text-xs font-semibold uppercase tracking-wide text-brand-600">{item.categoryName}</p>
        <Link to={`/items/${item.id}`} className="mt-1 block truncate text-lg font-bold hover:text-brand-600">{item.name}</Link>
        <p className="mt-1 flex items-center gap-1 text-sm text-slate-500"><MapPin className="h-3.5 w-3.5" />{item.location}</p>
        <div className="mt-4 flex items-center justify-between border-t border-slate-100 pt-4">
          <span className="text-xl font-extrabold">{money(item.dailyRent)}<span className="text-sm font-medium text-slate-400"> /day</span></span>
          <span className="rounded-lg bg-slate-100 px-2 py-1 text-xs font-medium text-slate-600">{label(item.condition)}</span>
        </div>
      </div>
    </div>
  );
}
