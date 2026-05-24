/* ============================================================
   Metrics Exchange — API Client
   ============================================================
   Все обращения к backend централизованы здесь.
   Адрес backend меняется одной переменной API_BASE.
   ============================================================ */

var API_BASE = 'http://localhost:8080';

var Api = (function () {
  'use strict';

  /**
   * Отправляет POST-запрос с телом application/x-www-form-urlencoded.
   * Вложенные объекты превращаются в dot-notation:
   *   { companyFilter: { companyNameSubstring: 'foo' } }
   *   → companyFilter.companyNameSubstring=foo
   */
  async function postForm(url, data) {
    var params = new URLSearchParams();

    function flatten(obj, prefix) {
      for (var key in obj) {
        if (!Object.prototype.hasOwnProperty.call(obj, key)) continue;
        var val = obj[key];
        if (val === null || val === undefined || val === '') continue;
        var fullKey = prefix ? prefix + '.' + key : key;
        if (typeof val === 'object' && !Array.isArray(val)) {
          flatten(val, fullKey);
        } else {
          params.append(fullKey, String(val));
        }
      }
    }

    flatten(data, '');

    var res = await fetch(API_BASE + url, {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body: params.toString()
    });

    if (!res.ok) {
      var text = await res.text().catch(function () { return 'Нет деталей'; });
      throw new Error('HTTP ' + res.status + ': ' + text);
    }
    return res.json();
  }

  /** Отправляет POST-запрос с телом application/json. */
  async function postJson(url, data) {
    var res = await fetch(API_BASE + url, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data)
    });

    if (!res.ok) {
      var text = await res.text().catch(function () { return 'Нет деталей'; });
      throw new Error('HTTP ' + res.status + ': ' + text);
    }
    return res.json();
  }

  /** Отправляет GET-запрос и возвращает JSON. */
  async function get(url) {
    var res = await fetch(API_BASE + url);
    if (!res.ok) {
      var text = await res.text().catch(function () { return 'Нет деталей'; });
      throw new Error('HTTP ' + res.status + ': ' + text);
    }
    return res.json();
  }

  /**
   * Собирает объект CompanyFilter из opts, включая только заданные поля.
   * Поля: companyNameSubstring, companyId, suppUserProfileExchange.
   */
  function buildCompanyFilter(opts) {
    var f = {};
    if (opts.nameFilter) {
      f.companyNameSubstring = opts.nameFilter;
    }
    if (opts.companyId !== undefined && opts.companyId !== null && opts.companyId !== '') {
      f.companyId = opts.companyId;
    }
    if (opts.suppUserProfileExchange !== undefined && opts.suppUserProfileExchange !== null) {
      f.suppUserProfileExchange = opts.suppUserProfileExchange;
    }
    return f;
  }

  /** Отправляет DELETE-запрос. Тело ответа не парсится (эндпоинты возвращают 204). */
  async function del(url) {
    var res = await fetch(API_BASE + url, { method: 'DELETE' });
    if (!res.ok) {
      var text = await res.text().catch(function () { return 'Нет деталей'; });
      throw new Error('HTTP ' + res.status + ': ' + text);
    }
    return true;
  }

  return {

    /**
     * Получить компании (краткая сводка id + name) с пагинацией и фильтром.
     * POST /companies/id-name-summary (тело CompanyIdNameSummaryRq).
     * Возвращает плоский массив [{ id, name }] (не PageResponse).
     * @param {object} [opts]
     * @param {number}  [opts.pageNumber=0]
     * @param {number}  [opts.pageSize=1000]
     * @param {string}  [opts.nameFilter]              - подстрока названия (companyNameSubstring)
     * @param {number}  [opts.companyId]               - точный id компании
     * @param {boolean} [opts.suppUserProfileExchange] - фильтр поддержки переноса профилей
     */
    getCompaniesIdNameSummary: function (opts) {
      opts = opts || {};
      var companyFilter = buildCompanyFilter(opts);
      // Эндпоинт принимает @RequestBody → отправляем JSON
      return postJson('/user-exchange-metrics/companies/id-name-summary', {
        pageNumber: opts.pageNumber !== undefined ? opts.pageNumber : 0,
        pageSize:   opts.pageSize   !== undefined ? opts.pageSize   : 1000,
        companyFilter: companyFilter
      });
    },

    /**
     * Получить список компаний с пагинацией и фильтром.
     * @param {object} opts
     * @param {number}  opts.pageNumber
     * @param {number}  opts.pageSize
     * @param {string}  [opts.nameFilter]              - подстрока для фильтра по названию
     * @param {number}  [opts.companyId]               - точный id компании
     * @param {boolean} [opts.suppUserProfileExchange] - фильтр поддержки переноса профилей
     */
    getCompanies: function (opts) {
      opts = opts || {};
      var data = {
        pageNumber: opts.pageNumber !== undefined ? opts.pageNumber : 0,
        pageSize:   opts.pageSize   !== undefined ? opts.pageSize   : 10,
        companyFilter: buildCompanyFilter(opts)
      };
      // Эндпоинт принимает @RequestBody → отправляем JSON
      return postJson('/user-exchange-metrics/companies', data);
    },

    /**
     * Создать заявку на перенос рейтинга.
     * @param {object} payload - поля CreateTransferRqDto:
     *   fromProfileId, toProfileId, comment, fromCompanyId, toCompanyId
     */
    createTransferRequest: function (payload) {
      // Эндпоинт принимает @RequestBody → отправляем JSON
      return postJson('/user-exchange-metrics/transfer-request/create', payload);
    },

    /**
     * Получить список заявок с пагинацией (всегда сортировка DESC по дате).
     * @param {object} opts
     * @param {number} opts.pageNumber
     * @param {number} opts.pageSize
     */
    getTransferRequests: function (opts) {
      opts = opts || {};
      // Эндпоинт принимает @RequestBody → отправляем JSON
      return postJson('/user-exchange-metrics/transfer-requests', {
        pageNumber: opts.pageNumber !== undefined ? opts.pageNumber : 0,
        pageSize:   opts.pageSize   !== undefined ? opts.pageSize   : 10,
        sortOrder: 'DESC'
      });
    },

    /**
     * Получить подробную информацию об одной заявке.
     * @param {number|string} id
     */
    getTransferRequest: function (id) {
      return get('/user-exchange-metrics/transfer-request/' + id);
    },

    /**
     * Создать пользователя в системе.
     * @param {object} payload - поля CreateUserDto:
     *   email, role (UserRole), linkedCompanyId (необязательно)
     */
    createUser: function (payload) {
      // Эндпоинт принимает @RequestBody → отправляем JSON
      return postJson('/user-exchange-metrics/user/create', payload);
    },

    /**
     * Получить список пользователей с пагинацией и сортировкой.
     * @param {object} opts
     * @param {number} opts.pageNumber
     * @param {number} opts.pageSize
     * @param {string} [opts.sortBy]     - поле сортировки (UserSortField).
     *   Передаётся только если задано; иначе не отправляется, чтобы избежать
     *   ошибки десериализации пока enum UserSortField на backend не заполнен.
     * @param {string} opts.sortOrder    - 'ASC' | 'DESC'
     * @param {object} [opts.userFilter] - фильтр (UserFilter): linkedCompanyId,
     *   userRole, а также userId и userEmailSubstr (последние два backend пока
     *   игнорирует — их нужно добавить в UserFilter).
     */
    getUsers: function (opts) {
      opts = opts || {};
      var data = {
        pageNumber: opts.pageNumber !== undefined ? opts.pageNumber : 0,
        pageSize:   opts.pageSize   !== undefined ? opts.pageSize   : 10,
        sortOrder:  opts.sortOrder  || 'ASC'
      };
      if (opts.sortBy) {
        data.sortBy = opts.sortBy;
      }
      if (opts.userFilter) {
        data.userFilter = opts.userFilter;
      }
      // Эндпоинт принимает @RequestBody → отправляем JSON
      return postJson('/user-exchange-metrics/users', data);
    },

    /**
     * Удалить пользователя по id.
     * @param {number|string} userId
     */
    deleteUser: function (userId) {
      return del('/user-exchange-metrics/user/' + userId);
    },

    /**
     * Удалить компанию из системы по id.
     * Backend также удалит администраторов компании и откажет связанные заявки.
     * @param {number|string} companyId
     */
    deleteCompany: function (companyId) {
      return del('/user-exchange-metrics/company/' + companyId);
    },

    /**
     * Создать у компании точку для предоставления метрик.
     * @param {object} payload - поля CreateCompanyPointDto: url, format, companyId
     */
    createCompanyPoint: function (payload) {
      // Эндпоинт принимает @RequestBody → отправляем JSON
      return postJson('/user-exchange-metrics/company-point/create', payload);
    },

    /**
     * Получить список точек компании с пагинацией.
     * @param {object} opts
     * @param {number} opts.pageNumber
     * @param {number} opts.pageSize
     * @param {number} opts.companyId    - фильтр по компании (CompanyPointFilter)
     * @param {string} [opts.sortOrder]  - 'ASC' | 'DESC'
     */
    getCompanyPoints: function (opts) {
      opts = opts || {};
      var data = {
        pageNumber: opts.pageNumber !== undefined ? opts.pageNumber : 0,
        pageSize:   opts.pageSize   !== undefined ? opts.pageSize   : 10,
        sortOrder:  opts.sortOrder  || 'ASC',
        companyPointFilter: { companyId: opts.companyId }
      };
      // Эндпоинт принимает @RequestBody → отправляем JSON
      return postJson('/user-exchange-metrics/company-points', data);
    },

    /**
     * Получить подробную информацию о точке компании.
     * @param {number|string} companyPointId
     */
    getCompanyPoint: function (companyPointId) {
      return get('/user-exchange-metrics/company-point/' + companyPointId);
    },

    /**
     * Обновить точку компании (статус и/или url).
     * Эндпоинт без @RequestBody → отправляем form-encoded.
     * @param {number|string} companyPointId
     * @param {object} payload - поля CompanyPointUpdateDto:
     *   newPointStatus (PointStatus), newCompanyPointUrl
     */
    updateCompanyPoint: function (companyPointId, payload) {
      return postForm('/user-exchange-metrics/company-points/' + companyPointId, payload);
    }
  };
})();
