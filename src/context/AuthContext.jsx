import { createContext, useCallback, useContext, useEffect, useMemo, useState } from "react";
import { authApi } from "../api";
import { tokenStore } from "../api/client";

const AuthContext = createContext(null);

// The backend has no /me endpoint, so we read the JWT claims instead:
// sub = user id, email = email. (role is read if the backend adds a "role" claim.)
function decode(token) {
  try {
    const p = JSON.parse(atob(token.split(".")[1].replace(/-/g, "+").replace(/_/g, "/")));
    if (p.exp && p.exp * 1000 < Date.now()) return null;
    return { id: Number(p.sub), email: p.email, role: p.role || null, exp: p.exp };
  } catch {
    return null;
  }
}

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    const t = tokenStore.get();
    return t ? decode(t) : null;
  });

  const logout = useCallback(() => {
    tokenStore.clear();
    setUser(null);
  }, []);

  useEffect(() => {
    window.addEventListener("br:unauthorized", logout);
    return () => window.removeEventListener("br:unauthorized", logout);
  }, [logout]);

  // auto-logout when the (15 min) token expires
  useEffect(() => {
    if (!user?.exp) return;
    const ms = user.exp * 1000 - Date.now();
    const t = setTimeout(logout, Math.max(ms, 0));
    return () => clearTimeout(t);
  }, [user, logout]);

  const login = async (email, password) => {
    const res = await authApi.login({ email, password });
    tokenStore.set(res.accessToken);
    setUser(decode(res.accessToken));
  };

  const register = async (data) => {
    await authApi.register(data);
    await login(data.email, data.password);
  };

  const value = useMemo(() => ({ user, isAuth: !!user, isAdmin: user?.role === "ROLE_ADMIN" || user?.role === "ADMIN", login, register, logout }), [user, logout]);
  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export const useAuth = () => useContext(AuthContext);
