import { useEffect, useState } from "react";
import { Link, NavLink, Outlet, useNavigate } from "react-router-dom";
import { Bell, Heart, LogOut, Menu, PackageOpen, X } from "lucide-react";
import { useAuth } from "../context/AuthContext";
import { notificationApi } from "../api";

function Navbar() {
  const { isAuth, isAdmin, logout } = useAuth();
  const nav = useNavigate();
  const [open, setOpen] = useState(false);
  const [unread, setUnread] = useState(0);

  useEffect(() => {
    if (!isAuth) return setUnread(0);
    const load = () => notificationApi.unreadCount().then(setUnread).catch(() => {});
    load();
    const t = setInterval(load, 30000);
    return () => clearInterval(t);
  }, [isAuth]);

  const link = ({ isActive }) => `text-sm font-semibold transition hover:text-brand-600 ${isActive ? "text-brand-600" : "text-slate-700"}`;
  const links = (
    <>
      <NavLink to="/items" className={link} onClick={() => setOpen(false)}>Browse</NavLink>
      {isAuth && <NavLink to="/my-items" className={link} onClick={() => setOpen(false)}>My Items</NavLink>}
      {isAuth && <NavLink to="/bookings" className={link} onClick={() => setOpen(false)}>Bookings</NavLink>}
      {isAdmin && <NavLink to="/admin/categories" className={link} onClick={() => setOpen(false)}>Admin</NavLink>}
    </>
  );

  return (
    <header className="sticky top-0 z-40 border-b border-slate-200 bg-white/90 backdrop-blur">
      <div className="mx-auto flex h-16 max-w-7xl items-center justify-between px-4 sm:px-6">
        <Link to="/" className="flex items-center gap-2 text-xl font-extrabold">
          <span className="rounded-xl bg-brand-500 p-1.5"><PackageOpen className="h-5 w-5 text-white" /></span>Borrow<span className="text-brand-500">Rent</span>
        </Link>
        <nav className="hidden items-center gap-8 md:flex">{links}</nav>
        <div className="hidden items-center gap-3 md:flex">
          {isAuth ? (
            <>
              <Link to="/wishlist" className="rounded-full p-2 hover:bg-slate-100" aria-label="Wishlist"><Heart className="h-5 w-5" /></Link>
              <Link to="/notifications" className="relative rounded-full p-2 hover:bg-slate-100" aria-label="Notifications">
                <Bell className="h-5 w-5" />
                {unread > 0 && <span className="absolute right-0.5 top-0.5 flex h-4 min-w-4 items-center justify-center rounded-full bg-brand-500 px-1 text-[10px] font-bold text-white">{unread}</span>}
              </Link>
              <button className="btn-outline !py-2" onClick={() => { logout(); nav("/"); }}><LogOut className="h-4 w-4" />Logout</button>
            </>
          ) : (
            <>
              <Link to="/login" className="btn-outline !py-2">Log in</Link>
              <Link to="/register" className="btn-primary !py-2">Sign up</Link>
            </>
          )}
        </div>
        <button className="md:hidden" onClick={() => setOpen(!open)} aria-label="Menu">{open ? <X /> : <Menu />}</button>
      </div>
      {open && (
        <div className="flex flex-col gap-4 border-t border-slate-100 bg-white px-6 py-5 md:hidden">
          {links}
          {isAuth ? (
            <>
              <NavLink to="/wishlist" className={link} onClick={() => setOpen(false)}>Wishlist</NavLink>
              <NavLink to="/notifications" className={link} onClick={() => setOpen(false)}>Notifications {unread > 0 && `(${unread})`}</NavLink>
              <button className="btn-outline" onClick={() => { logout(); setOpen(false); nav("/"); }}>Logout</button>
            </>
          ) : (
            <div className="flex gap-3"><Link to="/login" className="btn-outline flex-1" onClick={() => setOpen(false)}>Log in</Link><Link to="/register" className="btn-primary flex-1" onClick={() => setOpen(false)}>Sign up</Link></div>
          )}
        </div>
      )}
    </header>
  );
}

function Footer() {
  return (
    <footer className="mt-24 bg-ink text-slate-300">
      <div className="mx-auto grid max-w-7xl gap-10 px-6 py-14 md:grid-cols-4">
        <div className="md:col-span-2">
          <p className="text-xl font-extrabold text-white">Borrow<span className="text-brand-500">Rent</span></p>
          <p className="mt-3 max-w-sm text-sm">The peer-to-peer marketplace where neighbours rent out what they own and borrow what they need.</p>
        </div>
        <div><p className="mb-3 font-semibold text-white">Explore</p><ul className="space-y-2 text-sm"><li><Link to="/items" className="hover:text-white">Browse items</Link></li><li><Link to="/my-items/new" className="hover:text-white">List an item</Link></li></ul></div>
        <div><p className="mb-3 font-semibold text-white">Account</p><ul className="space-y-2 text-sm"><li><Link to="/login" className="hover:text-white">Log in</Link></li><li><Link to="/register" className="hover:text-white">Sign up</Link></li></ul></div>
      </div>
      <div className="border-t border-slate-700 py-5 text-center text-xs">© {new Date().getFullYear()} BorrowRent. All rights reserved.</div>
    </footer>
  );
}

export default function Layout() {
  return (
    <div className="flex min-h-screen flex-col">
      <Navbar />
      <main className="flex-1"><Outlet /></main>
      <Footer />
    </div>
  );
}
