import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { ArrowRight, CalendarCheck, Search, ShieldCheck, Sparkles, Wallet } from "lucide-react";
import { categoryApi, itemApi } from "../api";
import ItemCard from "../components/ItemCard";
import { Spinner } from "../components/ui";
import { useWishlist } from "../hooks";

export default function Home() {
  const nav = useNavigate();
  const [q, setQ] = useState("");
  const [cat, setCat] = useState("");
  const [cats, setCats] = useState([]);
  const [items, setItems] = useState(null);
  const wish = useWishlist();

  useEffect(() => {
    categoryApi.list().then(setCats).catch(() => {});
    itemApi.list({ status: "AVAILABLE", size: 8, sort: "createdAt,desc" }).then((p) => setItems(p.content)).catch(() => setItems([]));
  }, []);

  const search = (e) => {
    e.preventDefault();
    const p = new URLSearchParams();
    if (q) p.set("name", q);
    if (cat) p.set("categoryId", cat);
    nav(`/items?${p}`);
  };

  return (
    <>
      <section className="relative overflow-hidden bg-ink">
        <img src="https://images.unsplash.com/photo-1506619216599-9d16d0903dfd?auto=format&fit=crop&w=1800&q=60" alt="" className="absolute inset-0 h-full w-full object-cover opacity-30" />
        <div className="absolute inset-0 bg-gradient-to-r from-ink via-ink/80 to-transparent" />
        <div className="relative mx-auto max-w-7xl px-6 py-24 md:py-32">
          <span className="inline-flex items-center gap-2 rounded-full bg-white/10 px-4 py-1.5 text-sm font-medium text-brand-100"><Sparkles className="h-4 w-4" />Rent anything, from anyone nearby</span>
          <h1 className="mt-6 max-w-3xl text-4xl font-extrabold leading-tight text-white md:text-6xl">Don't buy it. <span className="text-brand-500">Borrow it.</span></h1>
          <p className="mt-5 max-w-xl text-lg text-slate-300">Cameras, tools, bikes, camping gear and more — rent from people around you, or earn by renting out what you already own.</p>

          <form onSubmit={search} className="mt-10 grid max-w-4xl gap-3 rounded-2xl bg-white p-3 shadow-2xl md:grid-cols-[1fr_220px_auto]">
            <div className="relative"><Search className="absolute left-4 top-3 h-5 w-5 text-slate-400" /><input value={q} onChange={(e) => setQ(e.target.value)} placeholder="What do you need? e.g. drill, camera…" className="input !border-0 !pl-12 focus:!ring-0" /></div>
            <select value={cat} onChange={(e) => setCat(e.target.value)} className="input !border-0 md:!border-l md:!rounded-none"><option value="">All categories</option>{cats.map((c) => <option key={c.id} value={c.id}>{c.name}</option>)}</select>
            <button className="btn-primary !px-8">Search</button>
          </form>
        </div>
      </section>

      <section className="mx-auto max-w-7xl px-6 pt-16">
        <div className="mb-8 flex items-end justify-between"><h2 className="text-2xl font-extrabold">Browse by category</h2><Link to="/items" className="flex items-center gap-1 text-sm font-semibold text-brand-600">View all <ArrowRight className="h-4 w-4" /></Link></div>
        <div className="flex flex-wrap gap-3">
          {cats.length === 0 && <p className="text-sm text-slate-500">No categories yet.</p>}
          {cats.map((c) => <Link key={c.id} to={`/items?categoryId=${c.id}`} className="rounded-full border border-slate-200 bg-white px-5 py-2.5 text-sm font-semibold shadow-sm transition hover:border-brand-500 hover:text-brand-600">{c.name}</Link>)}
        </div>
      </section>

      <section className="mx-auto max-w-7xl px-6 pt-16">
        <div className="mb-8 flex items-end justify-between"><div><h2 className="text-2xl font-extrabold">Fresh listings</h2><p className="text-slate-500">Newly added items ready to rent</p></div><Link to="/items" className="hidden items-center gap-1 text-sm font-semibold text-brand-600 sm:flex">See more <ArrowRight className="h-4 w-4" /></Link></div>
        {items === null ? <Spinner /> : items.length === 0 ? <p className="card p-10 text-center text-slate-500">No items listed yet. <Link className="font-semibold text-brand-600" to="/my-items/new">Be the first to list one!</Link></p> : (
          <div className="grid gap-6 sm:grid-cols-2 lg:grid-cols-4">{items.map((i) => <ItemCard key={i.id} item={i} wished={wish.ids.has(i.id)} onToggleWish={wish.toggle} />)}</div>
        )}
      </section>

      <section className="mx-auto max-w-7xl px-6 pt-20">
        <h2 className="text-center text-3xl font-extrabold">How it works</h2>
        <div className="mt-10 grid gap-6 md:grid-cols-3">
          {[
            [Search, "1. Find", "Search listings by name, category, price and location."],
            [CalendarCheck, "2. Request", "Pick your dates and send a booking request to the owner."],
            [Wallet, "3. Rent & return", "Pick up once approved, use it, and return it when done."],
          ].map(([Icon, t, d]) => (
            <div key={t} className="card p-8 text-center"><div className="mx-auto mb-4 flex h-14 w-14 items-center justify-center rounded-2xl bg-brand-50"><Icon className="h-7 w-7 text-brand-500" /></div><h3 className="text-lg font-bold">{t}</h3><p className="mt-2 text-sm text-slate-500">{d}</p></div>
          ))}
        </div>
      </section>

      <section className="mx-auto mt-20 max-w-7xl px-6">
        <div className="flex flex-col items-center justify-between gap-6 rounded-3xl bg-gradient-to-r from-brand-500 to-brand-700 p-10 text-white md:flex-row md:p-14">
          <div><h2 className="text-3xl font-extrabold">Got something sitting idle?</h2><p className="mt-2 flex items-center gap-2 text-brand-100"><ShieldCheck className="h-5 w-5" />List it in minutes and start earning.</p></div>
          <Link to="/my-items/new" className="btn bg-white !px-8 text-brand-700 hover:bg-brand-50">List your item</Link>
        </div>
      </section>
    </>
  );
}
