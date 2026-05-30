/* ============================================================
   Metrics Exchange — API Client
   ============================================================
   Все обращения к backend централизованы здесь.
   Адрес backend — переменная API_BASE (объявлена в auth.js).
   Аутентификация: cookie-based (Spring formLogin + JSESSIONID),
   поэтому все запросы используют credentials: 'include'.
   ============================================================ */

var Api = (function () {
  'use strict';

  /** На 401 — сброс профиля и переход на index.html?login=1. */
  function handleUnauthorized() {
    if (typeof Auth !== 'undefined') {
      Auth.clearUser();
      Auth.redirectToLogin();
    }
  }

  /** Бросает ошибку с телом ответа, если статус не ok. Иначе возвращает JSON. */
  async function handle(res) {
    if (res.status === 401) {
      handleUnauthorized();
      throw new Error('Требуется авторизация');
    }
    if (!res.ok) {
      var text = await res.text().catch(function () { return 'Нет деталей'; });
      throw new Error('HTTP ' + res.status + ': ' + text);
    }
    return res.json();
  }

  /**
   * Отправляет form-encoded запрос (по умолчанию POST).
   * Вложенные объекты — dot-notation: { a: { b: 1 } } → a.b=1
   */
  async function sendForm(method, url, data) {
    var params = new URLSearchParams();

    function flatten(obj, prefix) {
      for (var key in obj) {
        if (!Object.prototype.hasOwnProperty.call(obj, key)) continue;
        var val = obj[key];
        if (val === null || val === undefined || val === '') continue;
        var fullKey = prefix ? prefix + '.' + key : key;
        if (typeof val === 'object' && !Array.isArray(val)) flatten(val, fullKey);
        else params.append(fullKey, String(val));
      }
    }
    flatten(data, '');

    var res = await fetch(API_BASE + url, {
      method: method,
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body: params.toString(),
      credentials: 'include'
    });
    return handle(res);
  }
  function postForm(url, data) { return sendForm('POST', url, data); }
  function putForm(url, data)  { return sendForm('PUT',  url, data); }

  /** Отправляет JSON-запрос. */
  async function sendJson(method, url, data) {
    var res = await fetch(API_BASE + url, {
      method: method,
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data),
      credentials: 'include'
    });
    return handle(res);
  }
  function postJson(url, data) { return sendJson('POST', url, data); }
  function putJson(url, data)  { return sendJson('PUT',  url, data); }

  /** POST без тела (для эндпоинтов, читающих только path/query). */
  async function postEmpty(url) {
    var res = await fetch(API_BASE + url, { method: 'POST', credentials: 'include' });
    return handle(res);
  }

  /** GET-запрос. */
  async function get(url) {
    var res = await fetch(API_BASE + url, { credentials: 'include' });
    return handle(res);
  }

  /** DELETE-запрос. Возвращает true при успехе (204). */
  async function del(url) {
    var res = await fetch(API_BASE + url, { method: 'DELETE', credentials: 'include' });
    if (res.status === 401) {
      handleUnauthorized();
      throw new Error('Требуется авторизация');
    }
    if (!res.ok) {
      var text = await res.text().catch(function () { return 'Нет деталей'; });
      throw new Error('HTTP ' + res.status + ': ' + text);
    }
    return true;
  }

  function buildCompanyFilter(opts) {
    var f = {};
    if (opts.nameFilter) f.companyNameSubstring = opts.nameFilter;
    if (opts.companyId !== undefined && opts.companyId !== null && opts.companyId !== '') f.companyId = opts.companyId;
    if (opts.suppUserProfileExchange !== undefined && opts.suppUserProfileExchange !== null) {
      f.suppUserProfileExchange = opts.suppUserProfileExchange;
    }
    return f;
  }

  return {

    /* ========================================================
       Аутентификация / профиль
       ======================================================== */

    /**
     * Вход через Spring Security formLogin.
     * POST /user-exchange-metrics/login с form-encoded username + password.
     * При 200 — сессионная cookie установлена backend'ом.
     * При 401 — выбрасывается ошибка "Неверный логин или пароль".
     *
     * ВАЖНО на backend:
     *   .formLogin(f -> f
     *       .loginProcessingUrl("/user-exchange-metrics/login")
     *       .successHandler((req,res,a) -> res.setStatus(200))
     *       .failureHandler((req,res,e) -> res.setStatus(401))
     *   )
     */
    login: async function (username, password) {
      var body = new URLSearchParams();
      body.append('username', username);
      body.append('password', password);
      var res = await fetch(API_BASE + '/user-exchange-metrics/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: body.toString(),
        credentials: 'include'
      });
      if (res.status === 401) throw new Error('Неверный логин или пароль');
      if (!res.ok) throw new Error('HTTP ' + res.status);
      return true;
    },

    /** GET /me — возвращает профиль текущего пользователя. */
    getMe: function () {
      return get('/user-exchange-metrics/me');
    },

    /**
     * POST /logout — завершить сессию на backend.
     * Не показывает 401-редирект; используется из Auth.logout().
     */
    logout: function () {
      return fetch(API_BASE + '/user-exchange-metrics/logout', {
        method: 'POST',
        credentials: 'include'
      });
    },

    /**
     * Публичная регистрация клиента.
     * Эндпоинт сам выставляет роль CLIENT — указывать её в payload не нужно.
     * @param {object} payload - { email, password }
     */
    register: function (payload) {
      return postJson('/user-exchange-metrics/user/register-client', payload);
    },

    /* ========================================================
       Компании
       ======================================================== */

    getCompaniesIdNameSummary: function (opts) {
      opts = opts || {};
      return postJson('/user-exchange-metrics/companies/id-name-summary', {
        pageNumber: opts.pageNumber !== undefined ? opts.pageNumber : 0,
        pageSize:   opts.pageSize   !== undefined ? opts.pageSize   : 1000,
        companyFilter: buildCompanyFilter(opts)
      });
    },

    getCompanies: function (opts) {
      opts = opts || {};
      return postJson('/user-exchange-metrics/companies', {
        pageNumber: opts.pageNumber !== undefined ? opts.pageNumber : 0,
        pageSize:   opts.pageSize   !== undefined ? opts.pageSize   : 10,
        companyFilter: buildCompanyFilter(opts)
      });
    },

    createCompany: function (payload) {
      return postJson('/user-exchange-metrics/company/create', payload);
    },

    /**
     * Получить подробную информацию о компании.
     * @param {number|string} companyId
     */
    getCompany: function (companyId) {
      return get('/user-exchange-metrics/company/' + companyId);
    },

    /**
     * Обновить данные компании.
     * @param {number|string} companyId
     * @param {object} payload - поля для обновления (например: name, description,
     *   suppUserProfileExchange и т.п. — зависит от UpdateCompanyDto на backend).
     */
    updateCompany: function (companyId, payload) {
      return putJson('/user-exchange-metrics/company/' + companyId, payload);
    },

    deleteCompany: function (companyId) {
      return del('/user-exchange-metrics/company/' + companyId);
    },

    /* ========================================================
       Заявки на перенос
       ======================================================== */

    createTransferRequest: function (payload) {
      return postJson('/user-exchange-metrics/transfer-request/create', payload);
    },

    getTransferRequests: function (opts) {
      opts = opts || {};
      var data = {
        pageNumber: opts.pageNumber !== undefined ? opts.pageNumber : 0,
        pageSize:   opts.pageSize   !== undefined ? opts.pageSize   : 10,
        sortOrder:  opts.sortOrder || 'DESC'
      };
      if (opts.transferFilter) data.transferFilter = opts.transferFilter;
      if (opts.sortSpecifiers && opts.sortSpecifiers.length) data.sortOrderSpecifiers = opts.sortSpecifiers;
      return postJson('/user-exchange-metrics/transfer-requests', data);
    },

    getTransferRequest: function (id) {
      return get('/user-exchange-metrics/transfer-request/' + id);
    },

    makeTransferDecision: function (id, payload) {
      return postJson('/user-exchange-metrics/transfer-request/' + id + '/decision', payload);
    },

    /* ========================================================
       Пользователи
       ======================================================== */

    createUser: function (payload) {
      return postJson('/user-exchange-metrics/user/create', payload);
    },

    getUsers: function (opts) {
      opts = opts || {};
      var data = {
        pageNumber: opts.pageNumber !== undefined ? opts.pageNumber : 0,
        pageSize:   opts.pageSize   !== undefined ? opts.pageSize   : 10
      };
      if (opts.userFilter) data.userFilter = opts.userFilter;
      if (opts.sortSpecifiers && opts.sortSpecifiers.length) data.sortOrderSpecifiers = opts.sortSpecifiers;
      return postJson('/user-exchange-metrics/users', data);
    },

    deleteUser: function (id) {
      return del('/user-exchange-metrics/user/' + id);
    },

    /* ========================================================
       Точки компаний
       ======================================================== */

    createCompanyPoint: function (payload) {
      return postJson('/user-exchange-metrics/company-point/create', payload);
    },

    getCompanyPoints: function (opts) {
      opts = opts || {};
      var data = {
        pageNumber: opts.pageNumber !== undefined ? opts.pageNumber : 0,
        pageSize:   opts.pageSize   !== undefined ? opts.pageSize   : 10,
        sortOrder:  opts.sortOrder || 'ASC'
      };
      /* companyId передаётся только если явно задан — иначе backend подставит
         id компании пользователя из сессии. */
      if (opts.companyId !== undefined && opts.companyId !== null && opts.companyId !== '') {
        data.companyPointFilter = { companyId: opts.companyId };
      }
      return postJson('/user-exchange-metrics/company-points', data);
    },

    getCompanyPoint: function (id) {
      return get('/user-exchange-metrics/company-point/' + id);
    },

    updateCompanyPoint: function (id, payload) {
      return putForm('/user-exchange-metrics/company-points/' + id, payload);
    },

    /* ========================================================
       Альянсы
       ======================================================== */

    createAlliance: function (payload) {
      return postJson('/user-exchange-metrics/alliance/create', payload);
    },

    getAlliances: function (opts) {
      opts = opts || {};
      var allianceFilter = {};
      if (opts.nameFilter) allianceFilter.allianceNameSubstring = opts.nameFilter;
      if (opts.allianceId !== undefined && opts.allianceId !== null && opts.allianceId !== '') {
        allianceFilter.allianceId = opts.allianceId;
      }
      return postJson('/user-exchange-metrics/alliances', {
        pageNumber: opts.pageNumber !== undefined ? opts.pageNumber : 0,
        pageSize:   opts.pageSize   !== undefined ? opts.pageSize   : 10,
        allianceFilter: allianceFilter
      });
    },

    getAlliance: function (id) {
      return get('/user-exchange-metrics/alliance/' + id);
    },

    updateAlliance: function (id, payload) {
      return putForm('/user-exchange-metrics/alliance/' + id, payload);
    },

    deleteAlliance: function (id) {
      return del('/user-exchange-metrics/alliance/' + id);
    },

    /* ========================================================
       Точки альянсов
       ======================================================== */

    createAlliancePoint: function (payload) {
      return postJson('/user-exchange-metrics/alliance-point/create', payload);
    },

    getAlliancePoint: function (id) {
      return get('/user-exchange-metrics/alliance-point/' + id);
    },

    updateAlliancePoint: function (id, payload) {
      return putJson('/user-exchange-metrics/alliance-point/' + id, payload);
    },

    deleteAlliancePoint: function (id) {
      return del('/user-exchange-metrics/alliance-point/' + id);
    },

    addCompanyPointToAlliancePoint: function (apId, cpId) {
      return postEmpty('/user-exchange-metrics/alliance-point/' + apId +
        '/add-company-point?companyPointId=' + encodeURIComponent(cpId));
    },

    deleteCompanyPointFromAlliancePoint: function (apId, cpId) {
      return postEmpty('/user-exchange-metrics/alliance-point/' + apId +
        '/delete-company-point?companyPointId=' + encodeURIComponent(cpId));
    }
  };
})();
