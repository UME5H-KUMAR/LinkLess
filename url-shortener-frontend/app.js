const API_BASE = (window.LINKLESS_CONFIG?.apiBase || "http://localhost:8080/api/v1").replace(/\/$/, "");
const SESSION_KEY = "linkless-session";

const state = {
  authMode: "login",
  session: readSession(),
  urls: [],
  selectedShortUrl: "",
  busy: false,
};

const els = {
  authView: document.querySelector("#authView"),
  dashboardView: document.querySelector("#dashboardView"),
  authForm: document.querySelector("#authForm"),
  loginTab: document.querySelector("#loginTab"),
  signupTab: document.querySelector("#signupTab"),
  emailGroup: document.querySelector("#emailGroup"),
  username: document.querySelector("#username"),
  email: document.querySelector("#email"),
  password: document.querySelector("#password"),
  authSubmit: document.querySelector("#authSubmit"),
  signedInLabel: document.querySelector("#signedInLabel"),
  logoutButton: document.querySelector("#logoutButton"),
  shortenForm: document.querySelector("#shortenForm"),
  originalUrl: document.querySelector("#originalUrl"),
  shortenSubmit: document.querySelector("#shortenSubmit"),
  refreshUrls: document.querySelector("#refreshUrls"),
  urlList: document.querySelector("#urlList"),
  selectedShortUrl: document.querySelector("#selectedShortUrl"),
  startDate: document.querySelector("#startDate"),
  endDate: document.querySelector("#endDate"),
  loadAnalytics: document.querySelector("#loadAnalytics"),
  totalClickCount: document.querySelector("#totalClickCount"),
  eventCount: document.querySelector("#eventCount"),
  analyticsList: document.querySelector("#analyticsList"),
  toast: document.querySelector("#toast"),
};

function readSession() {
  try {
    return JSON.parse(localStorage.getItem(SESSION_KEY));
  } catch {
    return null;
  }
}

function saveSession(session) {
  state.session = session;
  localStorage.setItem(SESSION_KEY, JSON.stringify(session));
}

function clearSession() {
  state.session = null;
  state.urls = [];
  state.selectedShortUrl = "";
  localStorage.removeItem(SESSION_KEY);
}

async function apiRequest(path, options = {}) {
  let response;

  try {
    response = await fetch(`${API_BASE}${path}`, {
      ...options,
      headers: {
        "Content-Type": "application/json",
        ...(state.session?.jwt ? { Authorization: `Bearer ${state.session.jwt}` } : {}),
        ...options.headers,
      },
    });
  } catch {
    throw new Error(`Cannot reach backend at ${API_BASE}. Check that Spring Boot is running and restart it after CORS changes.`);
  }

  const text = await response.text();
  let data = null;

  if (text) {
    try {
      data = JSON.parse(text);
    } catch {
      data = text;
    }
  }

  if (!response.ok) {
    throw new Error(typeof data === "string" ? data : data?.message || `Request failed with status ${response.status}`);
  }

  return data;
}

function showToast(message, type = "success") {
  els.toast.textContent = message;
  els.toast.classList.toggle("error", type === "error");
  els.toast.classList.remove("hidden");

  window.clearTimeout(showToast.timer);
  showToast.timer = window.setTimeout(() => {
    els.toast.classList.add("hidden");
  }, 3200);
}

function setBusy(isBusy, label = "") {
  state.busy = isBusy;
  const disabled = Boolean(isBusy);

  els.authSubmit.disabled = disabled;
  els.shortenSubmit.disabled = disabled;
  els.refreshUrls.disabled = disabled;
  els.loadAnalytics.disabled = disabled || !state.selectedShortUrl;

  if (label === "auth") els.authSubmit.textContent = isBusy ? "Working..." : state.authMode === "signup" ? "Create account" : "Login";
  if (label === "shorten") els.shortenSubmit.textContent = isBusy ? "Shortening..." : "Shorten URL";
  if (label === "analytics") els.loadAnalytics.textContent = isBusy ? "Loading..." : "Load analytics";
}

function setAuthMode(mode) {
  state.authMode = mode;
  const isSignup = mode === "signup";

  els.loginTab.classList.toggle("active", !isSignup);
  els.signupTab.classList.toggle("active", isSignup);
  els.emailGroup.classList.toggle("hidden", !isSignup);
  els.email.required = isSignup;
  els.password.autocomplete = isSignup ? "new-password" : "current-password";
  els.authSubmit.textContent = isSignup ? "Create account" : "Login";
}

function setDefaultDates() {
  const end = new Date();
  const start = new Date();
  start.setDate(end.getDate() - 7);
  els.startDate.value = start.toISOString().slice(0, 10);
  els.endDate.value = end.toISOString().slice(0, 10);
}

function renderShell() {
  const loggedIn = Boolean(state.session?.jwt);
  els.authView.classList.toggle("hidden", loggedIn);
  els.dashboardView.classList.toggle("hidden", !loggedIn);

  if (loggedIn) {
    els.signedInLabel.textContent = `Signed in as ${state.session.username}`;
    refreshUrls();
  }
}

function renderUrls() {
  els.urlList.innerHTML = "";
  els.selectedShortUrl.textContent = state.selectedShortUrl || "No URL selected";
  els.loadAnalytics.disabled = state.busy || !state.selectedShortUrl;

  if (!state.urls.length) {
    els.urlList.innerHTML = '<p class="empty-state">No links yet. Create your first Linkless URL above.</p>';
    return;
  }

  state.urls.forEach((item) => {
    const card = document.createElement("article");
    card.className = `url-card${item.shortUrl === state.selectedShortUrl ? " selected" : ""}`;

    const main = document.createElement("button");
    main.className = "url-main";
    main.type = "button";
    main.innerHTML = `<strong>${escapeHtml(item.shortUrl)}</strong><span>${escapeHtml(item.originalUrl)}</span>`;
    main.addEventListener("click", () => {
      state.selectedShortUrl = item.shortUrl;
      renderUrls();
      clearAnalytics();
    });

    const actions = document.createElement("div");
    actions.className = "url-actions";

    const clickCount = document.createElement("span");
    clickCount.textContent = `${item.clickCount || 0} clicks`;

    const copy = document.createElement("button");
    copy.type = "button";
    copy.textContent = "Copy";
    copy.addEventListener("click", () => copyShortUrl(item.shortUrl));

    const open = document.createElement("a");
    open.href = `${API_BASE}/${item.shortUrl}`;
    open.target = "_blank";
    open.rel = "noreferrer";
    open.textContent = "Open";

    actions.append(clickCount, copy, open);
    card.append(main, actions);
    els.urlList.append(card);
  });
}

function clearAnalytics() {
  els.totalClickCount.textContent = "0";
  els.eventCount.textContent = "0";
  els.analyticsList.innerHTML = '<p class="empty-state">Load analytics for the selected date range.</p>';
}

async function handleAuthSubmit(event) {
  event.preventDefault();
  setBusy(true, "auth");

  const username = els.username.value.trim();
  const password = els.password.value;

  try {
    if (state.authMode === "signup") {
      await apiRequest("/auth/signup", {
        method: "POST",
        body: JSON.stringify({
          username,
          email: els.email.value.trim(),
          password,
        }),
      });
    }

    const loginData = await apiRequest("/auth/login", {
      method: "POST",
      body: JSON.stringify({ username, password }),
    });

    saveSession({ jwt: loginData.jwt, userId: loginData.userId, username });
    els.authForm.reset();
    showToast(`Welcome, ${username}.`);
    renderShell();
  } catch (error) {
    showToast(error.message, "error");
  } finally {
    setBusy(false, "auth");
  }
}

async function refreshUrls() {
  if (!state.session?.jwt) return;

  setBusy(true);
  try {
    state.urls = (await apiRequest("/urls/myUrls")) || [];
    if (!state.selectedShortUrl || !state.urls.some((item) => item.shortUrl === state.selectedShortUrl)) {
      state.selectedShortUrl = state.urls[0]?.shortUrl || "";
    }
    renderUrls();
    clearAnalytics();
  } catch (error) {
    showToast(error.message, "error");
  } finally {
    setBusy(false);
  }
}

async function handleShorten(event) {
  event.preventDefault();
  setBusy(true, "shorten");

  try {
    const created = await apiRequest("/urls/shorten", {
      method: "POST",
      body: JSON.stringify({ originalUrl: els.originalUrl.value.trim() }),
    });

    state.urls = [created, ...state.urls.filter((item) => item.id !== created.id)];
    state.selectedShortUrl = created.shortUrl;
    els.shortenForm.reset();
    renderUrls();
    clearAnalytics();
    showToast("Linkless URL created.");
  } catch (error) {
    showToast(error.message, "error");
  } finally {
    setBusy(false, "shorten");
  }
}

async function loadAnalytics() {
  if (!state.selectedShortUrl) return;

  setBusy(true, "analytics");

  const startDate = `${els.startDate.value}T00:00:00`;
  const endDate = `${els.endDate.value}T23:59:59`;
  const params = new URLSearchParams({ startDate, endDate });

  try {
    const [events, totals] = await Promise.all([
      apiRequest(`/urls/analytics/${state.selectedShortUrl}?${params}`),
      apiRequest(`/urls/totalClicks?${params}`),
    ]);

    renderAnalytics(events || [], totals || {});
  } catch (error) {
    showToast(error.message, "error");
  } finally {
    setBusy(false, "analytics");
  }
}

function renderAnalytics(events, totals) {
  const totalClicks = Object.values(totals).reduce((sum, count) => sum + Number(count), 0);

  els.totalClickCount.textContent = String(totalClicks);
  els.eventCount.textContent = String(events.length);
  els.analyticsList.innerHTML = "";

  if (!Object.keys(totals).length && !events.length) {
    els.analyticsList.innerHTML = '<p class="empty-state">No clicks found for this date range.</p>';
    return;
  }

  Object.entries(totals).forEach(([date, count]) => {
    els.analyticsList.append(createMetricRow(date, `${count} total clicks`));
  });

  events.forEach((event) => {
    els.analyticsList.append(createMetricRow(formatDateTime(event.clickDate), `${event.count || 1} event`));
  });
}

function createMetricRow(leftText, rightText) {
  const row = document.createElement("div");
  row.className = "metric-row";

  const left = document.createElement("span");
  left.textContent = leftText;

  const right = document.createElement("strong");
  right.textContent = rightText;

  row.append(left, right);
  return row;
}

async function copyShortUrl(shortUrl) {
  const fullUrl = `${API_BASE}/${shortUrl}`;

  try {
    await navigator.clipboard.writeText(fullUrl);
    showToast("Short link copied.");
  } catch {
    showToast(fullUrl);
  }
}

function logout() {
  clearSession();
  renderShell();
  clearAnalytics();
  showToast("You are logged out.");
}

function formatDateTime(value) {
  if (!value) return "";
  return value.replace("T", " ").slice(0, 19);
}

function escapeHtml(value) {
  return String(value || "")
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#039;");
}

els.loginTab.addEventListener("click", () => setAuthMode("login"));
els.signupTab.addEventListener("click", () => setAuthMode("signup"));
els.authForm.addEventListener("submit", handleAuthSubmit);
els.shortenForm.addEventListener("submit", handleShorten);
els.refreshUrls.addEventListener("click", refreshUrls);
els.loadAnalytics.addEventListener("click", loadAnalytics);
els.logoutButton.addEventListener("click", logout);

setDefaultDates();
setAuthMode("login");
clearAnalytics();
renderShell();
