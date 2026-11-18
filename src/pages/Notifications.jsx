import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import toast from "react-hot-toast";
import { Bell } from "lucide-react";
import { notificationApi } from "../api";
import { errorMessage } from "../api/client";
import { Empty, PageHeader, Spinner } from "../components/ui";

export default function Notifications() {
  const [list, setList] = useState(null);
  useEffect(() => { notificationApi.list().then(setList).catch((e) => { toast.error(errorMessage(e)); setList([]); }); }, []);
  const read = async (n) => {
    if (n.read) return;
    try { await notificationApi.markRead(n.id); setList((l) => l.map((x) => (x.id === n.id ? { ...x, read: true } : x))); } catch (e) { toast.error(errorMessage(e)); }
  };
  return (
    <div className="mx-auto max-w-3xl px-6 py-10">
      <PageHeader title="Notifications" subtitle="Updates on your bookings." />
      {!list ? <Spinner /> : list.length === 0 ? <Empty icon={Bell} title="You're all caught up" text="Booking updates will show up here." /> : (
        <div className="space-y-3">{[...list].sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt)).map((n) => (
          <div key={n.id} onClick={() => read(n)} className={`card flex cursor-pointer gap-4 p-5 transition ${n.read ? "opacity-60" : "border-l-4 border-brand-500"}`}>
            <div className="flex-1"><p className="font-bold">{n.title}</p><p className="mt-1 text-sm text-slate-600">{n.message}</p>
              <p className="mt-2 text-xs text-slate-400">{new Date(n.createdAt).toLocaleString()} {n.bookingId && <>· <Link to="/bookings" className="text-brand-600">View bookings</Link></>}</p></div>
            {!n.read && <span className="mt-1 h-2.5 w-2.5 rounded-full bg-brand-500" />}
          </div>))}</div>
      )}
    </div>
  );
}
