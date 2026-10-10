# NetOps Hub — Backend Architecture & API Documentation

## Overview
NetOps Hub Backend is built on **Cloudflare Workers** and **Cloudflare D1** SQLite database providing a high-performance, low-latency REST API (`/api/v1/`) with strict Role-Based Access Control (RBAC: Admin, Editor, Viewer).

## Infrastructure Stack
- **Runtime:** Cloudflare Workers (TypeScript)
- **Database:** Cloudflare D1 (Serverless Distributed SQLite)
- **Authentication:** SHA-256 password hashing + Web Crypto session tokens
- **Auditing:** Immutable append-only audit trail in D1

## REST API Endpoints

### System
- `GET /api/v1/health` — Service health check & D1 connectivity test

### Authentication
- `POST /api/v1/auth/login` — Login with username/email & password, returns Bearer token and user info
- `GET /api/v1/auth/me` — Get authenticated user details & role
- `POST /api/v1/auth/logout` — Invalidate session

### Company Information
- `GET /api/v1/sites` — List all infrastructure sites / branches
- `GET /api/v1/vcenters` — List all vCenter servers
- `GET /api/v1/servers?site=&status=&q=` — Query servers with multi-criteria search
- `GET /api/v1/rahkaran` — List Rahkaran ERP deployments, ports, and web links

### Network Academy
- `GET /api/v1/academy/categories` — List all Academy categories
- `GET /api/v1/academy/articles?category=&difficulty=&q=` — Query comprehensive educational articles
- `GET /api/v1/academy/articles/:id` — Retrieve full structured article details
- `POST /api/v1/academy/articles` — Create and publish article directly to Cloudflare D1
- `PUT /api/v1/academy/articles/:id` — Edit and update article in Cloudflare D1
- `DELETE /api/v1/academy/articles/:id` — Remove article from Cloudflare D1 (Admin only)
- `POST /api/v1/academy/bulk-sync` — Batch synchronize articles from Android app to D1
- `GET /api/v1/academy/sources` — Curated live official documentation portals (MS Learn, Cisco, MikroTik, Fortinet, Wireshark, VMware)
- `GET /api/v1/academy/ports?q=` — Search TCP/UDP port database
- `GET /api/v1/academy/errors` — Search HTTP status codes
- `GET /api/v1/academy/wireshark` — Search Wireshark display filters catalog

### User Features (Authenticated)
- `GET /api/v1/favorites` / `POST /api/v1/favorites` / `DELETE /api/v1/favorites/:articleId`
- `GET /api/v1/notes` / `POST /api/v1/notes`

### Administration (Admin Only)
- `GET /api/v1/audit-logs` — Review audit trail

## Deployment Instructions

To deploy to Cloudflare Workers with Wrangler:
```bash
cd backend
npm install
# Initialize D1 schema
npx wrangler d1 execute netops-hub-d1 --file=./schema.sql
# Run migrations & seed data
npx wrangler d1 execute netops-hub-d1 --file=./migrations/0001_initial.sql
# Deploy Worker
npx wrangler deploy
```
