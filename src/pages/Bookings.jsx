import { useCallback, useEffect, useState } from "react";
import { Link } from "react-router-dom";
import toast from "react-hot-toast";
import { CalendarX2 } from "lucide-react";
import { bookingApi } from "../api";
import { errorMessage } from "../api/client";
import { Badge, Empty, PageHeader, Spinner } from "../components/ui";
import { fmtDate, money } from "../utils";

function Row({ b, role, onAct }) {
  const acts = [];
  if (role === "borrower" && ["PENDING", "APPROVED"].includes(b.status)) acts.push(["cancel", "Cancel", "btn-danger"]);
  if (role === "owner") {
    if (b.status === "PENDING") acts.push(["approve", "Approve", "btn-primary"], ["reject", "Reject", "btn-danger"]);
    if (b.status === "APPROVED") acts.push(["activate", "Mark as picked up", "btn-dark"]);
    if (b.status === "ACTIVE") acts.push(["complete", "Mark as returned", "btn-dark"]);
  }
  return (
    <div className="card flex flex-col gap-4 p-5 md:flex-row md:items-center md:justify-between">
      <div className="space-y-1">
        <div className="flex items-center gap-3"><Link to={`/items/${b.itemId}`} className="text-lg font-bold hover:text-brand-600">{b.itemTitle}</Link><Badge value={b.status} /></div>
        <p className="text-sm text-slate-500">{fmtDate(b.startDate)} → {fmtDate(b.endDate)} · <span className="font-semibold text-ink">{money(b.totalAmount)}</span></p>
        {role === "owner" && <p className="text-sm text-slate-500">Requested by <span className="font-medium text-ink">{b.borrowerName}</span></p>}
        {b.message && <p className="text-sm italic text-slate-500">“{b.message}”</p>}
      </div>
      <div className="flex flex-wrap gap-2">{acts.map(([a, l, c]) => <button key={a} className={`${c} !py-2`} onClick={() => onAct(a, b.id)}>{l}</button>)}</div>
    </div>
  );
}

export default function Bookings() {
  const [tab, setTab] = useState("borrower");
  const [data, setData] = useState({ borrower: null, owner: null });

  const load = useCallback(() => {
    bookingApi.mine().then((d) => setData((s) => ({ ...s, borrower: d }))).catch((e) => { toast.error(errorMessage(e)); setData((s) => ({ ...s, borrower: [] })); });
    bookingApi.owner().then((d) => setData((s) => ({ ...s, owner: d }))).catch((e) => { toast.error(errorMessage(e)); setData((s) => ({ ...s, owner: [] })); });
  }, []);
  useEffect(load, [load]);

  const act = async (action, id) => {
    try { await bookingApi[action](id); toast.success(`Booking ${action}d`); load(); }
    catch (e) { toast.error(errorMessage(e)); }
  };

  const list = data[tab];
  const tabs = [["borrower", "My rentals"], ["owner", "Requests for my items"]];
  return (
    <div className="mx-auto max-w-5xl px-6 py-10">
      <PageHeader title="Bookings" subtitle="Track what you've rented and requests on your items." />
      <div className="mb-6 inline-flex rounded-xl bg-slate-200 p-1">
        {tabs.map(([k, l]) => <button key={k} onClick={() => setTab(k)} className={`rounded-lg px-5 py-2 text-sm font-semibold transition ${tab === k ? "bg-white shadow" : "text-slate-600"}`}>{l}{data[k] ? ` (${data[k].length})` : ""}</button>)}
      </div>
      {list === null ? <Spinner /> : list.length === 0 ? (
        <Empty icon={CalendarX2} title="Nothing here yet" text={tab === "borrower" ? "Browse items and send your first booking request." : "Requests will appear here when someone books your items."}>
          {tab === "borrower" && <Link to="/items" className="btn-primary">Browse items</Link>}
        </Empty>
      ) : <div className="space-y-4">{[...list].sort((a, b) => b.id - a.id).map((b) => <Row key={b.id} b={b} role={tab} onAct={act} />)}</div>}
    </div>
  );
}
