import { useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import toast from "react-hot-toast";
import { Heart, MapPin, ShieldCheck, Tag } from "lucide-react";
import { bookingApi, itemApi } from "../api";
import { errorMessage } from "../api/client";
import { useAuth } from "../context/AuthContext";
import { Badge, Spinner } from "../components/ui";
import { useWishlist } from "../hooks";
import { FALLBACK_IMG, days, label, money, today } from "../utils";

export default function ItemDetail() {
  const { id } = useParams();
  const nav = useNavigate();
  const { user, isAuth } = useAuth();
  const wish = useWishlist();
  const [item, setItem] = useState(null);
  const [err, setErr] = useState("");
  const [f, setF] = useState({ startDate: "", endDate: "", message: "" });
  const [busy, setBusy] = useState(false);

  useEffect(() => { itemApi.get(id).then(setItem).catch((e) => setErr(errorMessage(e, "Item not found"))); }, [id]);

  if (err) return <p className="py-32 text-center text-slate-500">{err}</p>;
  if (!item) return <Spinner className="py-40" />;

  const n = days(f.startDate, f.endDate);
  const total = n * Number(item.dailyRent);
  const isOwner = user?.id === item.ownerId;

  const submit = async (e) => {
    e.preventDefault();
    if (!isAuth) return nav("/login", { state: { from: `/items/${id}` } });
    setBusy(true);
    try {
      await bookingApi.create({ itemId: item.id, startDate: f.startDate, endDate: f.endDate, message: f.message || null });
      toast.success("Booking request sent to the owner!");
      nav("/bookings");
    } catch (e2) { toast.error(errorMessage(e2)); } finally { setBusy(false); }
  };

  return (
    <div className="mx-auto max-w-7xl px-6 py-10">
      <Link to="/items" className="text-sm font-medium text-slate-500 hover:text-brand-600">← Back to listings</Link>
      <div className="mt-5 grid gap-10 lg:grid-cols-[1fr_400px]">
        <div>
          <div className="overflow-hidden rounded-3xl bg-slate-200"><img src={item.imageUrl || FALLBACK_IMG} onError={(e) => (e.currentTarget.src = FALLBACK_IMG)} alt={item.name} className="aspect-[16/10] w-full object-cover" /></div>
          <div className="mt-6 flex flex-wrap items-center gap-3"><Badge value={item.status} /><span className="flex items-center gap-1 text-sm text-slate-500"><Tag className="h-4 w-4" />{item.categoryName}</span><span className="flex items-center gap-1 text-sm text-slate-500"><MapPin className="h-4 w-4" />{item.location}</span></div>
          <div className="mt-3 flex items-start justify-between gap-4"><h1 className="text-3xl font-extrabold">{item.name}</h1>
            <button onClick={() => wish.toggle(item.id)} className="btn-outline !px-3"><Heart className={`h-5 w-5 ${wish.ids.has(item.id) ? "fill-red-500 text-red-500" : ""}`} /></button></div>
          <div className="mt-6 grid grid-cols-2 gap-4 sm:grid-cols-3">
            <div className="card p-4"><p className="text-xs text-slate-500">Condition</p><p className="font-bold">{label(item.condition)}</p></div>
            <div className="card p-4"><p className="text-xs text-slate-500">Daily rent</p><p className="font-bold">{money(item.dailyRent)}</p></div>
            <div className="card p-4"><p className="text-xs text-slate-500">Listed</p><p className="font-bold">{new Date(item.createdAt).toLocaleDateString()}</p></div>
          </div>
          <h2 className="mt-8 text-xl font-bold">About this item</h2>
          <p className="mt-2 whitespace-pre-line leading-relaxed text-slate-600">{item.description}</p>
        </div>

        <aside className="card h-fit space-y-4 p-6 lg:sticky lg:top-24">
          <p className="text-3xl font-extrabold">{money(item.dailyRent)}<span className="text-base font-medium text-slate-400"> / day</span></p>
          {isOwner ? (
            <><p className="rounded-xl bg-brand-50 p-4 text-sm text-brand-700">This is your listing.</p><Link to={`/my-items/${item.id}/edit`} className="btn-dark w-full">Edit item</Link></>
          ) : (
            <form onSubmit={submit} className="space-y-4">
              <div className="grid grid-cols-2 gap-3">
                <div><label className="label">Start</label><input required type="date" min={today()} className="input" value={f.startDate} onChange={(e) => setF({ ...f, startDate: e.target.value })} /></div>
                <div><label className="label">End</label><input required type="date" min={f.startDate || today()} className="input" value={f.endDate} onChange={(e) => setF({ ...f, endDate: e.target.value })} /></div>
              </div>
              <div><label className="label">Message to owner (optional)</label><textarea maxLength={500} rows={3} className="input" value={f.message} onChange={(e) => setF({ ...f, message: e.target.value })} /></div>
              {n > 0 && <div className="space-y-1 rounded-xl bg-slate-50 p-4 text-sm"><div className="flex justify-between"><span>{money(item.dailyRent)} × {n} day{n > 1 && "s"}</span><span>{money(total)}</span></div><div className="flex justify-between border-t pt-2 text-base font-bold"><span>Estimated total</span><span>{money(total)}</span></div></div>}
              <button className="btn-primary w-full" disabled={busy || item.status !== "AVAILABLE"}>{item.status !== "AVAILABLE" ? "Currently unavailable" : busy ? "Sending…" : isAuth ? "Request to book" : "Log in to book"}</button>
              <p className="flex items-center justify-center gap-2 text-xs text-slate-500"><ShieldCheck className="h-4 w-4" />The owner must approve your request before the rental starts.</p>
            </form>
          )}
        </aside>
      </div>
    </div>
  );
}
