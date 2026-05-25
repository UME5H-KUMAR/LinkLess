# Linkless Handoff

## Project

Workspace: `C:\Projects\URL Shortener`

App name: **Linkless**

Purpose: URL shortener with login, per-user short links, redirects, click tracking, date-range analytics, and delete URL support.

Main folders:

- `url-shortener-sb` - Spring Boot backend
- `url-shortener-frontend` - plain HTML/CSS/JS frontend, no React/Vite/Tailwind

## Current Frontend

Folder: `url-shortener-frontend`

Files restored/created:

- `index.html`
- `styles.css`
- `app.js`
- `config.js`
- `server.js`
- `README.md`

Frontend features:

- Linkless branding
- Meta description: `Shorten your URL with analytics support.`
- Signup/login
- Create short URL
- List current user's URLs
- Copy short URL
- Open short URL
- Delete short URL
- Select date range for analytics
- Show total clicks in selected range and click events

Frontend API config:

```js
window.LINKLESS_CONFIG = {
  apiBase: "http://localhost:8080/api/v1",
};
```

For deployment, change `apiBase` to the deployed backend URL, for example:

```js
window.LINKLESS_CONFIG = {
  apiBase: "https://your-backend-domain.com/api/v1",
};
```

Run frontend locally:

```powershell
cd "C:\Projects\URL Shortener\url-shortener-frontend"
node server.js
```

Open:

```text
http://localhost:5173
```

## Current Backend

Folder: `url-shortener-sb`

Important backend endpoints:

- `POST /api/v1/auth/signup`
- `POST /api/v1/auth/login`
- `POST /api/v1/urls/shorten`
- `GET /api/v1/urls/myUrls`
- `GET /api/v1/urls/analytics/{shortUrl}?startDate=...&endDate=...`
- `GET /api/v1/urls/totalClicks?startDate=...&endDate=...`
- `DELETE /api/v1/urls/{urlId}`
- `GET /api/v1/{shortUrl}`

Delete URL support was restored in:

- `UrlMappingController.java`
- `UrlMappingService.java`
- `ClickEventRepository.java`

Expected delete behavior:

- Endpoint: `DELETE /api/v1/urls/{urlId}`
- Requires JWT auth
- Finds logged-in user from `Principal`
- Verifies the URL belongs to that user
- Deletes related `ClickEvent` records first
- Deletes `UrlMapping`
- Returns `204 No Content` if deleted
- Returns `404 Not Found` if URL ID does not exist

## Backend Config

`application.properties` should have local defaults with environment overrides:

```properties
spring.application.name=url-shortner-sb

spring.datasource.url=${SPRING_DATASOURCE_URL:jdbc:postgresql://localhost:5432/urlShortnerDB}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME:postgres}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD:6969}
spring.jpa.hibernate.ddl-auto=${SPRING_JPA_HIBERNATE_DDL_AUTO:update}
spring.jpa.show-sql=${SPRING_JPA_SHOW_SQL:false}
spring.jpa.properties.hibernate.format_sql=true

jwt.secret=${JWT_SECRET:jmg1GvazNGJf7HliSfaZU3r8Uur6Kkkb2CYNp2XHRDx}
jwt.expiration=${JWT_EXPIRATION:86400000}

server.port=${PORT:8080}
server.servlet.context-path=/api/v1

frontend.url=${FRONTEND_URL:http://localhost:5173,http://127.0.0.1:5173}
```

Why this matters:

- Earlier the backend crashed because `jwt.secret=${JWT_SECRET}` had no default.
- This restored version can run locally without manually setting environment variables.
- Deployment can still override all sensitive values.

## CORS / Security

`WebConfig.java` should exist in:

```text
url-shortener-sb/src/main/java/com/dev/tomato/url_shortener_sb/security/WebConfig.java
```

It should allow origins from `frontend.url`, split by comma, and allow:

```text
GET, POST, PUT, DELETE, OPTIONS
```

`WebSecurityConfig.java` should permit preflight:

```java
.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
```

This is required because browser requests with JWT and JSON trigger `OPTIONS` preflight. Without it, the browser may show `Failed to fetch`.

## Local Run Order

1. Start PostgreSQL.

Expected local DB:

```text
Database: urlShortnerDB
User: postgres
Password: 6969
Port: 5432
```

2. Start backend:

```powershell
cd "C:\Projects\URL Shortener\url-shortener-sb"
.\mvnw.cmd spring-boot:run
```

3. Start frontend:

```powershell
cd "C:\Projects\URL Shortener\url-shortener-frontend"
node server.js
```

4. Open:

```text
http://localhost:5173
```

## Known Local Issue

The Maven wrapper previously failed in this environment with:

```text
Cannot start maven from wrapper
```

This appears to be a local wrapper/PowerShell issue, not necessarily a Java code issue. If another agent continues, first try:

```powershell
cd "C:\Projects\URL Shortener\url-shortener-sb"
.\mvnw.cmd spring-boot:run
```

If it still fails, inspect/repair `mvnw.cmd` or install Maven and run:

```powershell
mvn spring-boot:run
```

## Deployment Plan Discussed

Preferred practice path:

- AWS EC2
- Docker / Docker Compose
- GitHub Actions SSH deploy
- RDS PostgreSQL
- Optional domain + HTTPS later

Recommended order:

1. Make Docker Compose work locally.
2. Deploy to EC2 privately using public IP.
3. Use RDS PostgreSQL.
4. Test signup/login/shorten/open/delete/analytics.
5. Add domain and HTTPS.
6. Update:
   - frontend `config.js`
   - backend `FRONTEND_URL`

## Important Warnings

- Do not use `spring.jpa.hibernate.ddl-auto=create` in deployment. It wipes data on restart.
- Use `update` for practice deployment, migrations later for production.
- Do not commit real production DB passwords or JWT secrets.
- Before `git pull`, commit or stash local work.
- The static frontend server is only for local dev. In production, serve frontend through Nginx/static hosting or a container.

## Verification Already Done

After restore:

```powershell
node --check app.js
node --check config.js
node --check server.js
```

All passed.

Backend compile was not fully verified due to the Maven wrapper issue above.
