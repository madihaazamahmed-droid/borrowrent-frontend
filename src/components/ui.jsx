import { Loader2 } from "lucide-react";
import { label } from "../utils";

export const Spinner = ({ className = "py-20" }) => (
  <div className={`flex justify-center ${className}`}><Loader2 className="h-8 w-8 animate-spin text-brand-500" /></div>
);

export const Empty = ({ icon: Icon, title, text, children }) => (
  <div className="card flex flex-col items-center px-6 py-16 text-center">
    {Icon && <div className="mb-4 rounded-full bg-brand-50 p-4"><Icon className="h-8 w-8 text-brand-500" /></div>}
    <h3 className="text-lg font-bold">{title}</h3>
    {text && <p className="mt-1 max-w-sm text-sm text-slate-500">{text}</p>}
    <div className="mt-5">{children}</div>
  </div>
);

const COLORS = {
  PENDING: "bg-amber-100 text-amber-700", APPROVED: "bg-sky-100 text-sky-700", ACTIVE: "bg-emerald-100 text-emerald-700",
  COMPLETED: "bg-slate-200 text-slate-700", REJECTED: "bg-red-100 text-red-600", CANCELLED: "bg-slate-100 text-slate-500",
  AVAILABLE: "bg-emerald-100 text-emerald-700", UNAVAILABLE: "bg-red-100 text-red-600", MAINTENANCE: "bg-amber-100 text-amber-700",
};
export const Badge = ({ value }) => (
  <span className={`rounded-full px-2.5 py-1 text-xs font-semibold ${COLORS[value] || "bg-slate-100 text-slate-600"}`}>{label(value)}</span>
);

export function Pagination({ page, totalPages, onChange }) {
  if (!totalPages || totalPages <= 1) return null;
  return (
    <div className="mt-8 flex items-center justify-center gap-3">
      <button className="btn-outline" disabled={page === 0} onClick={() => onChange(page - 1)}>Previous</button>
      <span className="text-sm text-slate-500">Page {page + 1} of {totalPages}</span>
      <button className="btn-outline" disabled={page + 1 >= totalPages} onClick={() => onChange(page + 1)}>Next</button>
    </div>
  );
}

export const PageHeader = ({ title, subtitle, children }) => (
  <div className="mb-8 flex flex-wrap items-end justify-between gap-4">
    <div><h1 className="text-3xl font-extrabold tracking-tight">{title}</h1>{subtitle && <p className="mt-1 text-slate-500">{subtitle}</p>}</div>
    {children}
  </div>
);
