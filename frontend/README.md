# Frontend — Metrics Exchange

Простой frontend на чистом HTML + CSS + Vanilla JavaScript.  
Никаких фреймворков, сборщиков и `node_modules` — только стандартный браузерный API.

---

## Структура проекта

```
frontend/
├── index.html               ← Главная страница (5 разделов)
├── css/
│   └── style.css            ← Все стили (CSS-переменные, компоненты)
├── js/
│   └── api.js               ← API-клиент (все fetch-запросы к backend)
└── pages/
    ├── user.html            ← Страница пользователя (реализована)
    ├── company-admin.html   ← Заглушка
    ├── company-owner.html   ← Заглушка
    ├── app-admin.html       ← Заглушка
    └── superuser.html       ← Заглушка
```

---

## Запуск

> **Важно:** файлы нельзя открывать напрямую через `file://` — браузер заблокирует
> fetch-запросы к backend из-за CORS-политики. Нужен локальный HTTP-сервер.

### Вариант 1 — Python (рекомендуется, не требует установки)

```bash
# Перейти в папку frontend
cd path/to/project/frontend

# Python 3
python -m http.server 3000

# Открыть в браузере:
# http://localhost:3000
```

### Вариант 2 — Node.js (если уже установлен)

```bash
npx serve frontend -p 3000
# или
npx http-server frontend -p 3000
```

### Вариант 3 — VS Code Live Server

Установить расширение **Live Server** (ritwickdey.liveserver),  
открыть `index.html`, нажать **Go Live** в статус-баре.

---

## Настройка адреса backend

Backend по умолчанию ожидается на `http://localhost:8080`.

Если нужно изменить адрес — отредактировать одну строку в `js/api.js`:

```javascript
var API_BASE = 'http://localhost:8080';  // ← изменить здесь
```

---

## Настройка CORS в backend

При запуске frontend на отдельном порту браузер блокирует запросы к backend.  
Нужно добавить CORS-конфигурацию в Spring Boot (папка `backend`):

```java
// backend/src/main/java/.../config/CorsConfig.java

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:3000")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*");
            }
        };
    }
}
```

---

## Реализованный функционал

### Главная страница (`index.html`)

Навигационное меню с переходом на 5 разделов.

---

### Страница пользователя (`pages/user.html`)

Три вкладки:

#### 📝 Создать заявку

Форма для создания заявки на перенос рейтинга профиля.

| Поле                    | Тип           | Описание                                                                  |
|-------------------------|---------------|---------------------------------------------------------------------------|
| Компания-источник       | Выпадающий список | Загружается из `POST /company-exchange-metrics/companies` (pageSize=1000) |
| Компания-назначение     | Выпадающий список | То же                                                                     |
| ID профиля-источника    | Текст         | `fromProfileId`                                                           |
| ID профиля-назначения   | Текст         | `toProfileId`                                                             |
| Комментарий             | Textarea      | `comment` (необязательно)                                                 |

Запрос: `POST /alliance-exchange-metrics/transfer-request/create`

#### 🏢 Компании

Список всех компаний с возможностью:
- **Фильтрации** по названию (поле `companyFilter.companyNameSubstring`)
- **Сортировки** по названию (`sortOrder`: ASC / DESC)
- **Пагинации** (10 компаний на страницу)

Запрос: `POST /company-exchange-metrics/companies`

#### 📋 Мои заявки

Таблица заявок с колонками: ID, Статус, Решение, Дата создания, Из компании, В компанию.  
Сортировка всегда по дате — новые сверху (`sortOrder: DESC`).

- **Пагинация** (10 заявок на страницу)
- **Клик по строке** → открывает модальное окно с полной информацией о заявке

Запросы:
- Список: `POST /alliance-exchange-metrics/transfer-requests`
- Детали: `GET /alliance-exchange-metrics/transfer-request/{id}`

---

### Остальные страницы

Заглушки «в разработке». Функционал будет добавлен в следующих итерациях.

---

## Технические детали

### Отправка данных на backend

Контроллеры без аннотации `@RequestBody` принимают данные как **form-encoded**
(`application/x-www-form-urlencoded`), а не как JSON.  
`api.js` автоматически преобразует объекты в плоский формат:
```
{ companyFilter: { companyNameSubstring: 'foo' } }
→ companyFilter.companyNameSubstring=foo
```

### Поля status и createdAt в заявках

В текущей версии backend `TransferRqDto` не содержит поля `status` и `createdAt`.  
Фронтенд отображает `—` для этих полей. Как только backend начнёт их возвращать —
значения появятся автоматически без изменений во frontend.

### Зависимости

Нет внешних зависимостей. Используются только браузерные API:
- `fetch` — HTTP-запросы
- `URLSearchParams` — form-encoded тело запроса
- `document.querySelector` — работа с DOM
