import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import toast from "react-hot-toast";
import { categoryApi, itemApi } from "../api";
import { errorMessage } from "../api/client";
import { PageHeader, Spinner } from "../components/ui";
import { CONDITIONS, STATUSES, label } from "../utils";

const blank = { name: "", description: "", dailyRent: "", condition: "GOOD", imageUrl: "", location: "", categoryId: "", status: "AVAILABLE" };

export default function ItemForm() {
  const { id } = useParams();
  const editing = !!id;
  const nav = useNavigate();
  const [cats, setCats] = useState([]);
  const [f, setF] = useState(blank);
  const [loading, setLoading] = useState(editing);
  const [busy, setBusy] = useState(false);

  useEffect(() => {
    categoryApi.list().then(setCats).catch(() => {});
    if (editing) itemApi.get(id).then((i) => { setF({ ...i, imageUrl: i.imageUrl || "", categoryId: i.categoryId }); setLoading(false); }).catch((e) => { toast.error(errorMessage(e)); nav("/my-items"); });
  }, [id]); // eslint-disable-line

  const set = (k) => (e) => setF({ ...f, [k]: e.target.value });
  const submit = async (e) => {
    e.preventDefault(); setBusy(true);
    const body = { name: f.name, description: f.description, dailyRent: Number(f.dailyRent), condition: f.condition, imageUrl: f.imageUrl || null, location: f.location, categoryId: Number(f.categoryId) };
    try {
      if (editing) await itemApi.update(id, { ...body, status: f.status }); else await itemApi.create(body);
      toast.success(editing ? "Item updated" : "Item listed!"); nav("/my-items");
    } catch (err) { toast.error(errorMessage(err)); } finally { setBusy(false); }
  };

  if (loading) return <Spinner className="py-40" />;
  return (
    <div className="mx-auto max-w-3xl px-6 py-10">
      <PageHeader title={editing ? "Edit item" : "List a new item"} subtitle="Great photos and honest descriptions get more bookings." />
      <form onSubmit={submit} className="card space-y-5 p-8">
        <div><label className="label">Item name</label><input required maxLength={120} className="input" value={f.name} onChange={set("name")} /></div>
        <div><label className="label">Description</label><textarea required maxLength={2000} rows={5} className="input" value={f.description} onChange={set("description")} /></div>
        <div className="grid gap-5 sm:grid-cols-2">
          <div><label className="label">Daily rent (₹)</label><input required type="number" min="0.01" step="0.01" className="input" value={f.dailyRent} onChange={set("dailyRent")} /></div>
          <div><label className="label">Category</label><select required className="input" value={f.categoryId} onChange={set("categoryId")}><option value="">Select…</option>{cats.map((c) => <option key={c.id} value={c.id}>{c.name}</option>)}</select></div>
          <div><label className="label">Condition</label><select className="input" value={f.condition} onChange={set("condition")}>{CONDITIONS.map((c) => <option key={c} value={c}>{label(c)}</option>)}</select></div>
          <div><label className="label">Location</label><input required maxLength={200} className="input" value={f.location} onChange={set("location")} /></div>
        </div>
        <div><label className="label">Image URL</label><input type="url" maxLength={500} placeholder="https://…" className="input" value={f.imageUrl} onChange={set("imageUrl")} />
          {f.imageUrl && <img src={f.imageUrl} alt="preview" className="mt-3 h-40 rounded-xl object-cover" onError={(e) => (e.currentTarget.style.display = "none")} />}</div>
        {editing && <div><label className="label">Status</label><select className="input" value={f.status} onChange={set("status")}>{STATUSES.map((s) => <option key={s} value={s}>{label(s)}</option>)}</select></div>}
        <div className="flex gap-3 pt-2"><button className="btn-primary" disabled={busy}>{busy ? "Saving…" : editing ? "Save changes" : "Publish listing"}</button><button type="button" className="btn-outline" onClick={() => nav(-1)}>Cancel</button></div>
      </form>
    </div>
  );
}
