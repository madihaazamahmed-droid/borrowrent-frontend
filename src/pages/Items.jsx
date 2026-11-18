import { useEffect, useState } from "react";
import { useSearchParams } from "react-router-dom";
import { PackageSearch, SlidersHorizontal } from "lucide-react";
import { categoryApi, itemApi } from "../api";
import ItemCard from "../components/ItemCard";
import { Empty, PageHeader, Pagination, Spinner } from "../components/ui";
import { useWishlist } from "../hooks";

const SORTS = [["createdAt,desc", "Newest"], ["dailyRent,asc", "Price: low to high"], ["dailyRent,desc", "Price: high to low"], ["name,asc", "Name A–Z"]];

export default function Items() {
  const [sp, setSp] = useSearchParams();
  const [cats, setCats] = useState([]);
  const [data, setData] = useState(null);
  const wish = useWishlist();

  const f = {
    name: sp.get("name") || "", categoryId: sp.get("categoryId") || "", minRent: sp.get("minRent") || "",
    maxRent: sp.get("maxRent") || "", status: sp.get("status") || "", sort: sp.get("sort") || "createdAt,desc", page: Number(sp.get("page") || 0),
  };
  const [form, setForm] = useState(f);
  useEffect(() => setForm(f), [sp.toString()]); // eslint-disable-line

  useEffect(() => { categoryApi.list().then(setCats).catch(() => {}); }, []);

  useEffect(() => {
    setData(null);
    const params = { size: 9, page: f.page, sort: f.sort };
    ["name", "categoryId", "minRent", "maxRent", "status"].forEach((k) => f[k] && (params[k] = f[k]));
    itemApi.list(params).then(setData).catch(() => setData({ content: [], totalPages: 0 }));
  }, [sp.toString()]); // eslint-disable-line

  const apply = (patch) => {
    const next = { ...form, ...patch };
    const p = new URLSearchParams();
    Object.entries(next).forEach(([k, v]) => v !== "" && v !== 0 && p.set(k, v));
    setSp(p);
  };
  const set = (k) => (e) => setForm({ ...form, [k]: e.target.value });

  return (
    <div className="mx-auto max-w-7xl px-6 py-10">
      <PageHeader title="Browse items" subtitle={data ? `${data.totalElements ?? data.content.length} items found` : "Loading…"} />
      <div className="grid gap-8 lg:grid-cols-[280px_1fr]">
        <form onSubmit={(e) => { e.preventDefault(); apply({ page: 0 }); }} className="card h-fit space-y-5 p-6 lg:sticky lg:top-24">
          <p className="flex items-center gap-2 font-bold"><SlidersHorizontal className="h-4 w-4" />Filters</p>
          <div><label className="label">Search</label><input className="input" placeholder="Item name" value={form.name} onChange={set("name")} /></div>
          <div><label className="label">Category</label><select className="input" value={form.categoryId} onChange={set("categoryId")}><option value="">All</option>{cats.map((c) => <option key={c.id} value={c.id}>{c.name}</option>)}</select></div>
          <div className="grid grid-cols-2 gap-3">
            <div><label className="label">Min ₹/day</label><input type="number" min="0" className="input" value={form.minRent} onChange={set("minRent")} /></div>
            <div><label className="label">Max ₹/day</label><input type="number" min="0" className="input" value={form.maxRent} onChange={set("maxRent")} /></div>
          </div>
          <div><label className="label">Availability</label><select className="input" value={form.status} onChange={set("status")}><option value="">Any</option><option value="AVAILABLE">Available</option><option value="UNAVAILABLE">Unavailable</option><option value="MAINTENANCE">Maintenance</option></select></div>
          <div><label className="label">Sort by</label><select className="input" value={form.sort} onChange={(e) => apply({ sort: e.target.value, page: 0 })}>{SORTS.map(([v, l]) => <option key={v} value={v}>{l}</option>)}</select></div>
          <button className="btn-primary w-full">Apply filters</button>
          <button type="button" className="btn-outline w-full" onClick={() => setSp({})}>Reset</button>
        </form>

        <div>
          {data === null ? <Spinner /> : data.content.length === 0 ? <Empty icon={PackageSearch} title="No items match your filters" text="Try widening your search or resetting the filters." /> : (
            <>
              <div className="grid gap-6 sm:grid-cols-2 xl:grid-cols-3">{data.content.map((i) => <ItemCard key={i.id} item={i} wished={wish.ids.has(i.id)} onToggleWish={wish.toggle} />)}</div>
              <Pagination page={data.number ?? f.page} totalPages={data.totalPages} onChange={(p) => { apply({ page: p }); window.scrollTo({ top: 0, behavior: "smooth" }); }} />
            </>
          )}
        </div>
      </div>
    </div>
  );
}
