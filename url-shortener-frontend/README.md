# Linkless Frontend

Plain HTML, CSS, and JavaScript client for the Spring Boot backend in `url-shortener-sb`.

Run locally:

```powershell
cd "C:\Projects\URL Shortener\url-shortener-frontend"
node server.js
```

Open `http://localhost:5173`.

For deployment, edit `config.js`:

```js
window.LINKLESS_CONFIG = {
  apiBase: "https://your-backend-domain.com/api/v1",
};
```
