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

  return {

    /**
     * Получить компании для выпадающего списка (краткая сводка id + name).
     * Возвращает плоский массив [{ id, name }] (не PageResponse).
     * @param {boolean} [suppUserProfileExchange] - если задан, фильтрует компании
     *   по поддержке переноса профилей пользователей. Если не задан — все компании.
     */
    getCompaniesIdNameSummary: function (suppUserProfileExchange) {
      var qs = '';
      if (suppUserProfileExchange !== undefined && suppUserProfileExchange !== null) {
        qs = '?suppUserProfileExchange=' + suppUserProfileExchange;
      }
      return get('/user-exchange-metrics/companies/id-name-summary' + qs);
    },

    /**
     * Получить компании, поддерживающие перенос профилей пользователей
     * (suppUserProfileExchange=true). Используется в форме заявки.
     * Возвращает плоский массив [{ id, name }].
     */
    getAllCompanies: function () {
      return get('/user-exchange-metrics/companies/id-name-summary?suppUserProfileExchange=true');
    },

    /**
     * Получить список компаний с пагинацией, фильтрацией и сортировкой.
     * @param {object} opts
     * @param {number} opts.pageNumber
     * @param {number} opts.pageSize
     * @param {string} opts.nameFilter   - подстрока для фильтра по названию
     * @param {string} opts.sortOrder    - 'ASC' | 'DESC'
     */
    getCompanies: function (opts) {
      opts = opts || {};
      var data = {
        pageNumber: opts.pageNumber !== undefined ? opts.pageNumber : 0,
        pageSize:   opts.pageSize   !== undefined ? opts.pageSize   : 10,
        sortOrder:  opts.sortOrder  || 'ASC'
      };
      if (opts.nameFilter) {
        data.companyFilter = { companyNameSubstring: opts.nameFilter };
      }
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
      // Эндпоинт принимает @RequestBody → отправляем JSON
      return postJson('/user-exchange-metrics/users', data);
    }
  };
})();
