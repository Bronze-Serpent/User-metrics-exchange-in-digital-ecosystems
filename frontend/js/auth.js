/* ============================================================
   Metrics Exchange — клиентская часть formLogin-аутентификации
   ============================================================
   Сценарий:
   - Сессия живёт на стороне backend через JSESSIONID-cookie.
   - Все fetch-запросы используют credentials: 'include' (cookie).
   - Профиль пользователя (из /me) хранится в sessionStorage
     для отображения роли/email и контроля доступа на UI.

   Формы входа и регистрации живут на index.html как отдельная
   вкладка "Вход". При попытке открыть приватную страницу без
   авторизации редиректит на index.html?login=1&next=<откуда>.

   Подключение на странице:
     <script src="../js/auth.js"></script>   ← ДО api.js
     <script src="../js/api.js"></script>
   ============================================================ */

var API_BASE = 'http://localhost:8080';   // используется и из api.js

var Auth = (function () {
  'use strict';

  var USER_KEY = 'me_user_info';

  /* ---- Профиль ---- */
  function getUser() {
    var s = sessionStorage.getItem(USER_KEY);
    if (!s) return null;
    try { return JSON.parse(s); } catch (e) { return null; }
  }
  function setUser(user)  { sessionStorage.setItem(USER_KEY, JSON.stringify(user)); }
  function clearUser()    { sessionStorage.removeItem(USER_KEY); }

  /* ---- Карты ролей и страниц ---- */
  var ROLE_HOMES = {
    CLIENT:        'user.html',
    COMPANY_AGENT: 'company-owner.html',
    COMPANY_ADMIN: 'company-admin.html',
    ADMIN:         'app-admin.html',
    SUPER_USER:    'superuser.html'
  };

  /* Какие роли допущены на каждую приватную страницу.
     null/отсутствие в карте = публичная страница. */
  var PAGE_ROLES = {
    'user.html':           ['CLIENT'],
    'company-owner.html':  ['COMPANY_AGENT'],
    'company-admin.html':  ['COMPANY_ADMIN'],
    'app-admin.html':      ['ADMIN'],
    'superuser.html':      ['SUPER_USER'],
    'alliance.html':       ['ADMIN', 'SUPER_USER'],
    'alliance-point.html': ['ADMIN', 'SUPER_USER']
  };

  var ROLE_LABELS = {
    CLIENT:        'Клиент',
    COMPANY_AGENT: 'Представитель компании',
    COMPANY_ADMIN: 'Администратор компании',
    ADMIN:         'Администратор приложения',
    SUPER_USER:    'Суперпользователь'
  };

  /* ---- Утилиты путей ---- */
  function currentPageName() {
    var path = window.location.pathname;
    var idx = path.lastIndexOf('/');
    var name = (idx >= 0) ? path.substring(idx + 1) : path;
    if (!name) name = 'index.html';
    return name;
  }
  function isPagesContext() {
    return window.location.pathname.indexOf('/pages/') !== -1;
  }
  function urlForPagesFile(file) {
    return isPagesContext() ? file : ('pages/' + file);
  }
  function urlForRootFile(file) {
    return isPagesContext() ? ('../' + file) : file;
  }
  function indexUrl() { return urlForRootFile('index.html'); }

  /** URL для редиректа на форму входа: index.html?login=1&next=<откуда> */
  function loginUrlWithNext() {
    var next = encodeURIComponent(window.location.pathname + window.location.search);
    return urlForRootFile('index.html') + '?login=1&next=' + next;
  }

  function homeForUser(user) {
    var role = user && user.role;
    var page = ROLE_HOMES[role];
    return page ? urlForPagesFile(page) : indexUrl();
  }

  /** Публичная страница — на ней не требуется авторизация. */
  function isPublicPage(page) {
    return page === 'index.html';
  }

  /** Разрешена ли страница для роли пользователя. */
  function isAllowed(page, user) {
    var allowed = PAGE_ROLES[page];
    if (!allowed) return true;
    return !!(user && allowed.indexOf(user.role) !== -1);
  }

  /* ---- Редиректы ---- */
  function redirectToLogin() {
    window.location.href = loginUrlWithNext();
  }

  function logout() {
    fetch(API_BASE + '/user-exchange-metrics/logout', {
      method: 'POST',
      credentials: 'include'
    }).finally(function () {
      clearUser();
      window.location.href = indexUrl();
    });
  }

  /* ---- Утилиты HTML ---- */
  function escHtml(s) {
    if (s === null || s === undefined) return '';
    return String(s)
      .replace(/&/g, '&amp;').replace(/</g, '&lt;')
      .replace(/>/g, '&gt;').replace(/"/g, '&quot;');
  }

  /* ---- Инжект блока пользователя в top-nav ---- */
  function injectTopNavUI() {
    var topNav = document.querySelector('.top-nav');
    if (!topNav) return;

    /* Удалим прежний инжект */
    topNav.querySelectorAll('[data-auth-injected]').forEach(function (el) { el.remove(); });

    var user = getUser();

    /* Прячем/показываем вкладку "Вход" (на index.html) */
    var loginTab = topNav.querySelector('.top-nav-btn[data-tab="login"]');
    if (loginTab) loginTab.style.display = user ? 'none' : '';

    if (!user) return;   /* гость — ничего не дописываем */

    /* Если на странице нет статической "На главную" (например, index.html) —
       первому инжектируемому блоку даём margin-left: auto, чтобы прижать группу вправо. */
    var hasHomeLink = !!topNav.querySelector('a.top-nav-home');

    /* Email + роль */
    var info = document.createElement('span');
    info.className = 'top-nav-userinfo';
    if (!hasHomeLink) info.style.marginLeft = 'auto';
    info.setAttribute('data-auth-injected', '');
    var roleLabel = ROLE_LABELS[user.role] || user.role;
    info.innerHTML = '<strong>' + escHtml(user.email) + '</strong> ' +
      '<span style="color:var(--text-muted)">(' + escHtml(roleLabel) + ')</span>';
    topNav.appendChild(info);

    /* Кнопка "Выйти" */
    var logoutBtn = document.createElement('button');
    logoutBtn.type = 'button';
    logoutBtn.className = 'top-nav-logout';
    logoutBtn.setAttribute('data-auth-injected', '');
    logoutBtn.textContent = 'Выйти';
    logoutBtn.addEventListener('click', logout);
    topNav.appendChild(logoutBtn);
  }

  /* ---- Основная логика на DOMContentLoaded ---- */
  document.addEventListener('DOMContentLoaded', function () {
    var page = currentPageName();
    var user = getUser();

    if (isPublicPage(page)) {
      injectTopNavUI();
      return;
    }

    if (!user) {
      redirectToLogin();
      return;
    }

    /* Вариант B: редирект только если текущая страница недоступна */
    if (!isAllowed(page, user)) {
      window.location.href = homeForUser(user);
      return;
    }

    injectTopNavUI();
  });

  return {
    getUser:         getUser,
    setUser:         setUser,
    clearUser:       clearUser,
    isAuthenticated: function () { return !!getUser(); },
    indexUrl:        indexUrl,
    homeForUser:     homeForUser,
    isAllowed:       isAllowed,
    redirectToLogin: redirectToLogin,
    logout:          logout,
    injectTopNavUI:  injectTopNavUI,
    ROLE_HOMES:      ROLE_HOMES,
    ROLE_LABELS:     ROLE_LABELS,
    PAGE_ROLES:      PAGE_ROLES
  };
})();
