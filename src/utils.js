export const money = (n) => `₹${Number(n ?? 0).toLocaleString("en-IN", { maximumFractionDigits: 2 })}`;
export const fmtDate = (d) => (d ? new Date(d).toLocaleDateString("en-GB", { day: "2-digit", month: "short", year: "numeric" }) : "—");
export const today = () => new Date().toISOString().slice(0, 10);
export const days = (a, b) => Math.max(0, Math.round((new Date(b) - new Date(a)) / 86400000));
export const CONDITIONS = ["NEW", "LIKE_NEW", "GOOD", "FAIR", "POOR"];
export const STATUSES = ["AVAILABLE", "UNAVAILABLE", "MAINTENANCE"];
export const label = (s) => (s ? s.replace(/_/g, " ").toLowerCase().replace(/^\w/, (c) => c.toUpperCase()) : "");
export const FALLBACK_IMG = "https://images.unsplash.com/photo-1553531889-e6cf4d692b1b?auto=format&fit=crop&w=800&q=60";
