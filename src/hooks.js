import { useCallback, useEffect, useState } from "react";
import toast from "react-hot-toast";
import { wishlistApi } from "./api";
import { errorMessage } from "./api/client";
import { useAuth } from "./context/AuthContext";

// Shared wishlist state: Set of item ids + toggle that calls POST/DELETE /wishlist/{itemId}
export function useWishlist() {
  const { isAuth } = useAuth();
  const [ids, setIds] = useState(new Set());

  useEffect(() => {
    if (!isAuth) return setIds(new Set());
    wishlistApi.list().then((l) => setIds(new Set(l.map((w) => w.itemId)))).catch(() => {});
  }, [isAuth]);

  const toggle = useCallback(async (itemId) => {
    if (!isAuth) return toast("Log in to save items to your wishlist");
    try {
      if (ids.has(itemId)) {
        await wishlistApi.remove(itemId);
        setIds((s) => { const n = new Set(s); n.delete(itemId); return n; });
      } else {
        await wishlistApi.add(itemId);
        setIds((s) => new Set(s).add(itemId));
        toast.success("Added to wishlist");
      }
    } catch (e) { toast.error(errorMessage(e)); }
  }, [ids, isAuth]);

  return { ids, toggle };
}
