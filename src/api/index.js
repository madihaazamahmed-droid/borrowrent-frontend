import client from "./client";

// ---- AUTH (/api/auth) ----
export const authApi = {
  register: (data) => client.post("/auth/register", data).then((r) => r.data), // {fullName,email,password,phone?,address?}
  login: (data) => client.post("/auth/login", data).then((r) => r.data), // -> {accessToken,tokenType,expiresIn}
  logout: () => client.post("/auth/logout"),
};

// ---- CATEGORIES (/api/categories) ----
export const categoryApi = {
  list: () => client.get("/categories").then((r) => r.data),
  get: (id) => client.get(`/categories/${id}`).then((r) => r.data),
  create: (data) => client.post("/categories", data).then((r) => r.data), // ADMIN only
};

// ---- ITEMS (/api/items) ----
export const itemApi = {
  // params: status, categoryId, name, minRent, maxRent, page, size, sort
  list: (params) => client.get("/items", { params }).then((r) => r.data), // Spring Page
  get: (id) => client.get(`/items/${id}`).then((r) => r.data),
  mine: (params) => client.get("/items/my-items", { params }).then((r) => r.data),
  create: (data) => client.post("/items", data).then((r) => r.data),
  update: (id, data) => client.put(`/items/${id}`, data).then((r) => r.data),
  remove: (id) => client.delete(`/items/${id}`),
};

// ---- BOOKINGS (/api/bookings) ----
export const bookingApi = {
  create: (data) => client.post("/bookings", data).then((r) => r.data), // {itemId,startDate,endDate,message?}
  get: (id) => client.get(`/bookings/${id}`).then((r) => r.data),
  mine: () => client.get("/bookings/my-bookings").then((r) => r.data), // as borrower
  owner: () => client.get("/bookings/owner-bookings").then((r) => r.data), // as item owner
  approve: (id) => client.patch(`/bookings/${id}/approve`).then((r) => r.data),
  reject: (id) => client.patch(`/bookings/${id}/reject`).then((r) => r.data),
  cancel: (id) => client.patch(`/bookings/${id}/cancel`).then((r) => r.data),
  activate: (id) => client.patch(`/bookings/${id}/activate`).then((r) => r.data),
  complete: (id) => client.patch(`/bookings/${id}/complete`).then((r) => r.data),
};

// ---- WISHLIST (/api/wishlist) ----
export const wishlistApi = {
  list: () => client.get("/wishlist").then((r) => r.data),
  add: (itemId) => client.post(`/wishlist/${itemId}`).then((r) => r.data),
  remove: (itemId) => client.delete(`/wishlist/${itemId}`),
};

// ---- NOTIFICATIONS (/api/notifications) ----
export const notificationApi = {
  list: () => client.get("/notifications").then((r) => r.data),
  unread: () => client.get("/notifications/unread").then((r) => r.data),
  unreadCount: () => client.get("/notifications/unread-count").then((r) => r.data),
  markRead: (id) => client.patch(`/notifications/${id}/read`),
};
