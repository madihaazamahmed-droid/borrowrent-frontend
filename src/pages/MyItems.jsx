import { useCallback, useEffect, useState } from "react";
import { Link } from "react-router-dom";
import toast from "react-hot-toast";
import { Package, Pencil, Plus, Trash2 } from "lucide-react";
import { itemApi } from "../api";
import { errorMessage } from "../api/client";
import { Badge, Empty, PageHeader, Pagination, Spinner } from "../components/ui";
import { FALLBACK_IMG, money } from "../utils";

export default function MyItems() {
  const [page, setPage] = useState(0);
  const [data, setData] = useState(null);
  const load = useCallback(() => itemApi.mine({ page, size: 8, sort: "createdAt,desc" }).then(setData).catch((e) => { toast.error(errorMessage(e)); setData({ content: [] }); }), [page]);
  useEffect(() => { setData(null); load(); }, [load]);

  const remove = async (id) => {
    if (!confirm("Delete this item? This cannot be undone.")) return;
    try { await itemApi.remove(id); toast.success("Item deleted"); load(); } catch (e) { toast.error(errorMessage(e)); }
  };

  return (
    <div className="mx-auto max-w-5xl px-6 py-10">
      <PageHeader title="My items" subtitle="Manage the items you rent out."><Link to="/my-items/new" className="btn-primary"><Plus className="h-4 w-4" />List new item</Link></PageHeader>
      {!data ? <Spinner /> : data.content.length === 0 ? <Empty icon={Package} title="You haven't listed anything" text="Turn your idle stuff into income."><Link to="/my-items/new" className="btn-primary">List your first item</Link></Empty> : (
        <>
          <div className="space-y-4">{data.content.map((i) => (
            <div key={i.id} className="card flex items-center gap-5 p-4">
              <img src={i.imageUrl || FALLBACK_IMG} onError={(e) => (e.currentTarget.src = FALLBACK_IMG)} alt="" className="h-20 w-28 rounded-xl object-cover" />
              <div className="min-w-0 flex-1"><Link to={`/items/${i.id}`} className="block truncate font-bold hover:text-brand-600">{i.name}</Link><p className="text-sm text-slate-500">{i.categoryName} · {money(i.dailyRent)}/day</p></div>
              <Badge value={i.status} />
              <Link to={`/my-items/${i.id}/edit`} className="btn-outline !px-3 !py-2" aria-label="Edit"><Pencil className="h-4 w-4" /></Link>
              <button onClick={() => remove(i.id)} className="btn-danger !px-3 !py-2" aria-label="Delete"><Trash2 className="h-4 w-4" /></button>
            </div>))}</div>
          <Pagination page={page} totalPages={data.totalPages} onChange={setPage} />
        </>
      )}
    </div>
  );
}
