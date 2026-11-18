import axios from "axios";

const TOKEN_KEY = "br_token";

export const tokenStore = {
  get: () => localStorage.getItem(TOKEN_KEY),
  set: (t) => localStorage.setItem(TOKEN_KEY, t),
  clear: () => localStorage.removeItem(TOKEN_KEY),
};

const client = axios.create({
  baseURL: import.meta.env.VITE_API_URL || "http://localhost:8081/api",
});

// Attach JWT to every request
client.interceptors.request.use((config) => {
  const token = tokenStore.get();
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

// Token lives 15 min on this backend: on 401 drop it and tell the app.
client.interceptors.response.use(
  (res) => res,
  (err) => {
    if (err.response?.status === 401 && tokenStore.get()) {
      tokenStore.clear();
      window.dispatchEvent(new Event("br:unauthorized"));
    }
    return Promise.reject(err);
  }
);

// Backend returns errors in 3 shapes: {message}, {error}, {message, errors:{field:msg}}
export function errorMessage(err, fallback = "Something went wrong") {
  const d = err?.response?.data;
  if (!d) return err?.message === "Network Error" ? "Cannot reach the server. Is the backend running on port 8081?" : fallback;
  if (d.errors) return Object.values(d.errors).join(" · ");
  return d.message || d.error || fallback;
}

export default client;
