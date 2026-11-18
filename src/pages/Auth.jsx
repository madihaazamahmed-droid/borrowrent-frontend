import { useState } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import toast from "react-hot-toast";
import { PackageOpen } from "lucide-react";
import { useAuth } from "../context/AuthContext";
import { errorMessage } from "../api/client";

function Shell({ title, subtitle, children, footer }) {
  return (
    <div className="grid min-h-[calc(100vh-4rem)] lg:grid-cols-2">
      <div className="relative hidden lg:block">
        <img src="https://images.unsplash.com/photo-1512917774080-9991f1c4c750?auto=format&fit=crop&w=1200&q=60" alt="" className="absolute inset-0 h-full w-full object-cover" />
        <div className="absolute inset-0 bg-gradient-to-t from-ink/90 to-ink/20" />
        <div className="absolute bottom-12 left-12 right-12 text-white"><h2 className="text-4xl font-extrabold">Rent smarter.<br />Own less. Do more.</h2><p className="mt-3 text-slate-300">Join the community sharing everything from drills to drones.</p></div>
      </div>
      <div className="flex items-center justify-center px-6 py-12">
        <div className="w-full max-w-md">
          <span className="mb-5 inline-flex rounded-xl bg-brand-500 p-2"><PackageOpen className="h-6 w-6 text-white" /></span>
          <h1 className="text-3xl font-extrabold">{title}</h1><p className="mb-8 mt-1 text-slate-500">{subtitle}</p>
          {children}<p className="mt-6 text-center text-sm text-slate-500">{footer}</p>
        </div>
      </div>
    </div>
  );
}

export function Login() {
  const { login } = useAuth();
  const nav = useNavigate();
  const from = useLocation().state?.from || "/";
  const [f, setF] = useState({ email: "", password: "" });
  const [busy, setBusy] = useState(false);
  const submit = async (e) => {
    e.preventDefault(); setBusy(true);
    try { await login(f.email, f.password); toast.success("Welcome back!"); nav(from, { replace: true }); }
    catch (err) { toast.error(err.response?.status === 401 ? "Invalid email or password" : errorMessage(err)); }
    finally { setBusy(false); }
  };
  return (
    <Shell title="Welcome back" subtitle="Log in to manage your rentals." footer={<>New here? <Link to="/register" className="font-semibold text-brand-600">Create an account</Link></>}>
      <form onSubmit={submit} className="space-y-5">
        <div><label className="label">Email</label><input required type="email" className="input" value={f.email} onChange={(e) => setF({ ...f, email: e.target.value })} /></div>
        <div><label className="label">Password</label><input required type="password" className="input" value={f.password} onChange={(e) => setF({ ...f, password: e.target.value })} /></div>
        <button className="btn-primary w-full" disabled={busy}>{busy ? "Logging in…" : "Log in"}</button>
      </form>
    </Shell>
  );
}

export function Register() {
  const { register } = useAuth();
  const nav = useNavigate();
  const [f, setF] = useState({ fullName: "", email: "", password: "", phone: "", address: "" });
  const [busy, setBusy] = useState(false);
  const set = (k) => (e) => setF({ ...f, [k]: e.target.value });
  const submit = async (e) => {
    e.preventDefault(); setBusy(true);
    try { await register({ ...f, phone: f.phone || null, address: f.address || null }); toast.success("Account created!"); nav("/"); }
    catch (err) { toast.error(errorMessage(err)); } finally { setBusy(false); }
  };
  return (
    <Shell title="Create your account" subtitle="Start renting and earning in minutes." footer={<>Already registered? <Link to="/login" className="font-semibold text-brand-600">Log in</Link></>}>
      <form onSubmit={submit} className="space-y-4">
        <div><label className="label">Full name</label><input required maxLength={100} className="input" value={f.fullName} onChange={set("fullName")} /></div>
        <div><label className="label">Email</label><input required type="email" className="input" value={f.email} onChange={set("email")} /></div>
        <div><label className="label">Password (min 8 characters)</label><input required type="password" minLength={8} maxLength={72} className="input" value={f.password} onChange={set("password")} /></div>
        <div className="grid grid-cols-2 gap-3"><div><label className="label">Phone</label><input maxLength={20} className="input" value={f.phone} onChange={set("phone")} /></div><div><label className="label">Address</label><input maxLength={500} className="input" value={f.address} onChange={set("address")} /></div></div>
        <button className="btn-primary w-full" disabled={busy}>{busy ? "Creating…" : "Sign up"}</button>
      </form>
    </Shell>
  );
}
