import { useEffect, useState } from "react";
import toast from "react-hot-toast";
import { categoryApi } from "../api";
import { errorMessage } from "../api/client";
import { PageHeader, Spinner } from "../components/ui";

export default function AdminCategories() {
  const [list, setList] = useState(null);
  const [f, setF] = useState({ name: "", description: "" });
  const load = () => categoryApi.list().then(setList).catch(() => setList([]));
  useEffect(() => { load(); }, []);
  const submit = async (e) => {
    e.preventDefault();
    try { await categoryApi.create({ name: f.name, description: f.description || null }); toast.success("Category created"); setF({ name: "", description: "" }); load(); }
    catch (err) { toast.error(err.response?.status === 409 ? "That category already exists" : errorMessage(err)); }
  };
  return (
    <div className="mx-auto max-w-4xl px-6 py-10">
      <PageHeader title="Categories" subtitle="Admin: create categories for listings." />
      <form onSubmit={submit} className="card mb-8 grid gap-4 p-6 sm:grid-cols-[1fr_2fr_auto]">
        <input required maxLength={100} placeholder="Name" className="input" value={f.name} onChange={(e) => setF({ ...f, name: e.target.value })} />
        <input maxLength={500} placeholder="Description (optional)" className="input" value={f.description} onChange={(e) => setF({ ...f, description: e.target.value })} />
        <button className="btn-primary">Add</button>
      </form>
      {!list ? <Spinner /> : <div className="card divide-y divide-slate-100">{list.map((c) => <div key={c.id} className="p-5"><p className="font-bold">{c.name}</p><p className="text-sm text-slate-500">{c.description || "—"}</p></div>)}</div>}
    </div>
  );
}
