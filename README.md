# BorrowRent – React frontend

React 18 + Vite + Tailwind + Axios + React Router, wired to the Spring Boot backend (`http://localhost:8081`).

## Run
1. Start MySQL and the backend (`./mvnw spring-boot:run` in `backend/`).
2. `npm install` then `npm run dev` → http://localhost:5173
   (Port 5173 is the only origin allowed by the backend's CORS config, so keep it.)

API base URL is in `.env` (`VITE_API_URL=http://localhost:8081/api`).

## Optional backend patch (admin menu)
The login response has no role and there is no `/me` endpoint, so the frontend reads the JWT claims.
To show the **Admin → Categories** page for admins, copy `backend-patch/JwtService.java` over
`backend/src/main/java/com/barrowrent/security/JwtService.java` (it adds one `role` claim to the token).
Without it everything else works; only the admin link is hidden.
Create the first admin by setting `role='ADMIN'` on a user row in MySQL, and add some categories
(POST /api/categories as admin) so items can be listed.

## Endpoints used
| Method | Endpoint | Auth | Used in |
|---|---|---|---|
| POST | /api/auth/register | public | Register |
| POST | /api/auth/login | public | Login (returns accessToken, 15 min) |
| POST | /api/auth/logout | yes | Logout |
| GET | /api/categories, /api/categories/{id} | public | Home, filters, item form |
| POST | /api/categories | ADMIN | Admin → Categories |
| GET | /api/items (status, categoryId, name, minRent, maxRent, page, size, sort) | public | Home, Browse |
| GET | /api/items/{id} | public | Item detail |
| GET | /api/items/my-items | yes | My Items |
| POST / PUT / DELETE | /api/items, /api/items/{id} | yes (owner) | Item form, My Items |
| POST | /api/bookings | yes | Item detail booking form |
| GET | /api/bookings/{id} | yes | api only |
| GET | /api/bookings/my-bookings | yes | Bookings → My rentals |
| GET | /api/bookings/owner-bookings | yes | Bookings → Requests |
| PATCH | /api/bookings/{id}/approve, reject, cancel, activate, complete | yes | Bookings actions |
| GET / POST / DELETE | /api/wishlist, /api/wishlist/{itemId} | yes | Wishlist, hearts |
| GET | /api/notifications, /unread, /unread-count | yes | Bell badge, Notifications |
| PATCH | /api/notifications/{id}/read | yes | Notifications |

All calls live in `src/api/index.js`; the Axios instance with the JWT interceptor is `src/api/client.js`.
