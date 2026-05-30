/* ============================================================
   Metrics Exchange — клиентская часть HTTP Basic Authentication
   ============================================================
   Браузер не показывает встроенный диалог Basic-auth в ответ на
   fetch/XMLHttpRequest (это by-design в спецификации Fetch).
   Поэтому показываем свою форму, кодируем (user:pass) в base64
   и сами кладём в заголовок Authorization.

   Использование на странице:
     <script src="../js/auth.js"></script>   ← подключить ДО api.js
     <script src="../js/api.js"></script>

   После этого:
   - При первом заходе (нет токена в sessionStorage) — показывается модалка.
   - Все запросы из api.js автоматически получают заголовок Authorization.
   - На 401 от backend токен сбрасывается и модалка показывается снова.
   - В .top-nav автоматически добавляется кнопка "Выйти".
   ============================================================ */

var Auth = (function () {
  'use strict';

  var STORAGE_KEY = 'me_basic_auth';

  function getToken()       { return sessionStorage.getItem(STORAGE_KEY); }
  function setToken(token)  { sessionStorage.setItem(STORAGE_KEY, token); }
  function clearToken()     { sessionStorage.removeItem(STORAGE_KEY); }

  /** base64(user:pass) с поддержкой не-ASCII в логине/пароле */
  function makeToken(user, pass) {
    return 'Basic ' + btoa(unescape(encodeURIComponent(user + ':' + pass)));
  }

  /** Создаёт модалку и кнопку "Выйти" в top-nav (если есть). Идемпотентно. */
  function ensureUI() {
    if (document.getElementById('authLoginModal')) return;

    var modalHtml =
      '<div class="modal-overlay" id="authLoginModal" role="dialog" aria-modal="true">' +
        '<div class="modal" style="max-width: 380px;">' +
          '<div class="modal-header">' +
            '<h3 class="modal-title">Вход в Metrics Exchange</h3>' +
          '</div>' +
          '<form id="authLoginForm" novalidate>' +
            '<div class="form-group" style="margin-bottom: 12px;">' +
              '<label class="form-label" for="authUser">Логин</label>' +
              '<input type="text" id="authUser" class="form-control" required autocomplete="username" />' +
            '</div>' +
            '<div class="form-group" style="margin-bottom: 14px;">' +
              '<label class="form-label" for="authPass">Пароль</label>' +
              '<input type="password" id="authPass" class="form-control" required autocomplete="current-password" />' +
            '</div>' +
            '<div id="authAlert"></div>' +
            '<div class="form-actions" style="justify-content: flex-end;">' +
              '<button type="submit" class="btn btn-primary">Войти</button>' +
            '</div>' +
          '</form>' +
        '</div>' +
      '</div>';

    var holder = document.createElement('div');
    holder.innerHTML = modalHtml;
    document.body.appendChild(holder.firstChild);

    document.getElementById('authLoginForm').addEventListener('submit', function (e) {
      e.preventDefault();
      var user = document.getElementById('authUser').value;
      var pass = document.getElementById('authPass').value;
      if (!user || !pass) return;
      setToken(makeToken(user, pass));
      hideLogin();
      /* Перезагружаем страницу — самый простой способ перезапустить все
         init-запросы (loadCompanies, loadUsers, ...) с новой авторизацией */
      window.location.reload();
    });

    /* Добавляем кнопку "Выйти" в правую часть верхней панели, если она есть */
    var topNav = document.querySelector('.top-nav');
    if (topNav) {
      var btn = document.createElement('button');
      btn.type = 'button';
      btn.className = 'top-nav-home';
      btn.style.cssText = 'background:none;border:none;cursor:pointer;font:inherit;';
      btn.textContent = 'Выйти';
      btn.addEventListener('click', function () { logout(); });
      topNav.appendChild(btn);
    }
  }

  function showLogin() {
    ensureUI();
    var el = document.getElementById('authLoginModal');
    el.classList.add('open');
    /* фокус на поле логина */
    setTimeout(function () {
      var u = document.getElementById('authUser');
      if (u) u.focus();
    }, 0);
  }

  function hideLogin() {
    var m = document.getElementById('authLoginModal');
    if (m) m.classList.remove('open');
  }

  /** Отметить, что предыдущая попытка не прошла (показать в alert модалки). */
  function showAuthError(message) {
    ensureUI();
    var el = document.getElementById('authAlert');
    if (el) el.innerHTML = '<div class="alert alert-error">' + message + '</div>';
  }

  function logout() {
    clearToken();
    window.location.reload();
  }

  /* При загрузке DOM: создать модалку, и если токена нет — показать её */
  document.addEventListener('DOMContentLoaded', function () {
    ensureUI();
    if (!getToken()) showLogin();
  });

  return {
    getToken:        getToken,
    isAuthenticated: function () { return !!getToken(); },
    showLogin:       showLogin,
    showAuthError:   showAuthError,
    logout:          logout
  };
})();
