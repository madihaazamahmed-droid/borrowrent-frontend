import { Route, Routes } from "react-router-dom";
import Layout from "./components/Layout";
import ProtectedRoute from "./components/ProtectedRoute";
import Home from "./pages/Home";
import Items from "./pages/Items";
import ItemDetail from "./pages/ItemDetail";
import { Login, Register } from "./pages/Auth";
import Bookings from "./pages/Bookings";
import MyItems from "./pages/MyItems";
import ItemForm from "./pages/ItemForm";
import Wishlist from "./pages/Wishlist";
import Notifications from "./pages/Notifications";
import AdminCategories from "./pages/AdminCategories";

const P = ({ children, admin }) => <ProtectedRoute admin={admin}>{children}</ProtectedRoute>;

export default function App() {
  return (
    <Routes>
      <Route element={<Layout />}>
        <Route index element={<Home />} />
        <Route path="items" element={<Items />} />
        <Route path="items/:id" element={<ItemDetail />} />
        <Route path="login" element={<Login />} />
        <Route path="register" element={<Register />} />
        <Route path="bookings" element={<P><Bookings /></P>} />
        <Route path="my-items" element={<P><MyItems /></P>} />
        <Route path="my-items/new" element={<P><ItemForm /></P>} />
        <Route path="my-items/:id/edit" element={<P><ItemForm /></P>} />
        <Route path="wishlist" element={<P><Wishlist /></P>} />
        <Route path="notifications" element={<P><Notifications /></P>} />
        <Route path="admin/categories" element={<P admin><AdminCategories /></P>} />
        <Route path="*" element={<p className="py-40 text-center text-slate-500">Page not found.</p>} />
      </Route>
    </Routes>
  );
}
