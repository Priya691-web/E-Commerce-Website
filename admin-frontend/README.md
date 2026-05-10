# FashionStore · Admin Frontend (React + Vite)

Separate admin dashboard. Calls the same Java/Servlet backend that powers the storefront via JSON endpoints under `/api/admin/*`. Uses session-cookie auth (same `JSESSIONID` as the storefront).

## Ports

| App         | Dev URL                              |
| ----------- | ------------------------------------ |
| Storefront  | http://localhost:8080/FashionStore   |
| Admin (this)| http://localhost:5173                |

## Setup

```bash
cd admin-frontend
npm install
npm run dev
```

Then start the Java backend on port 8080 (Tomcat). The Vite dev server proxies every `/api/*` request to `http://localhost:8080/FashionStore/api/*` and rewrites the cookie domain to `localhost`, so the JSESSIONID set by `/api/admin/login` is sent on subsequent requests automatically.

## Build for production

```bash
npm run build      # outputs to dist/
npm run preview    # serves the production build on :5173
```

Deploy `dist/` behind any static server (Nginx, S3+CloudFront, etc.) and reverse-proxy `/api/*` to the Java backend so cookies share the same origin.

## Backend endpoints

| Method | Path                  | Auth        | Purpose                       |
| ------ | --------------------- | ----------- | ----------------------------- |
| POST   | /api/admin/login      | public      | Email + password sign-in      |
| POST   | /api/admin/logout     | session     | Invalidate session            |
| GET    | /api/admin/me         | public*     | Returns current admin or 401  |
| GET    | /api/admin/dashboard  | admin       | Stats + recent orders         |
| GET    | /api/admin/orders     | admin       | Recent orders                 |
| GET    | /api/admin/products   | admin       | All products                  |
| GET    | /api/admin/users      | admin       | All users                     |

All admin-protected endpoints require `role = 'admin'` on the session user; otherwise the API returns `403 { success:false, message:"Admin access required" }`.
