import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import toast from "react-hot-toast";
import { Heart, MapPin } from "lucide-react";
import { wishlistApi } from "../api";
import { errorMessage } from "../api/client";
import { Badge, Empty, PageHeader, Spinner } from "../components/ui";
import { FALLBACK_IMG, money } from "../utils";

export default function Wishlist() {
  const [list, setList] = useState(null);
  useEffect(() => { wishlistApi.list().then(setList).catch((e) => { toast.error(errorMessage(e)); setList([]); }); }, []);
  const remove = async (itemId) => {
    try { await wishlistApi.remove(itemId); setList((l) => l.filter((w) => w.itemId !== itemId)); } catch (e) { toast.error(errorMessage(e)); }
  };
  return (
    <div className="mx-auto max-w-7xl px-6 py-10">
      <PageHeader title="My wishlist" subtitle="Items you've saved for later." />
      {!list ? <Spinner /> : list.length === 0 ? <Empty icon={Heart} title="Your wishlist is empty" text="Tap the heart on any item to save it here."><Link to="/items" className="btn-primary">Browse items</Link></Empty> : (
        <div className="grid gap-6 sm:grid-cols-2 lg:grid-cols-4">{list.map((w) => (
          <div key={w.wishlistId} className="card overflow-hidden">
            <Link to={`/items/${w.itemId}`}><img src={w.imageUrl || FALLBACK_IMG} onError={(e) => (e.currentTarget.src = FALLBACK_IMG)} alt={w.itemName} className="aspect-[4/3] w-full object-cover" /></Link>
            <div className="space-y-2 p-5">
              <Link to={`/items/${w.itemId}`} className="block truncate font-bold hover:text-brand-600">{w.itemName}</Link>
              <p className="flex items-center gap-1 text-sm text-slate-500"><MapPin className="h-3.5 w-3.5" />{w.location}</p>
              <div className="flex items-center justify-between"><span className="font-extrabold">{money(w.dailyRent)}<span className="text-xs font-medium text-slate-400">/day</span></span><Badge value={w.status} /></div>
              <button className="btn-danger w-full !py-2" onClick={() => remove(w.itemId)}>Remove</button>
            </div>
          </div>))}</div>
      )}
    </div>
  );
}
