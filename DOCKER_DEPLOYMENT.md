# TeachSync Docker Deployment

Этот файл описывает контейнерное развертывание TeachSync: инфраструктура, backend-микросервисы, API Gateway и frontend.

## Компоненты

Инфраструктура:

- `users-db` — PostgreSQL для пользователей;
- `course-db` — PostgreSQL для курсов, групп, категорий и тем;
- `schedule-db` — PostgreSQL для расписаний и аудиторий;
- `replacement-db` — PostgreSQL для заявок на замену;
- `notification-db` — PostgreSQL для уведомлений, настроек и активности;
- `zookeeper` и `kafka` — брокер событий;
- `kafka-ui` — веб-интерфейс для просмотра Kafka topics.

Backend:

- `auth-service` — аутентификация и JWT;
- `users-service` — пользователи, роли, специализации;
- `course-service` — курсы, группы, назначение преподавателей;
- `schedule-service` — расписание, аудитории, проверка конфликтов;
- `replacement-service` — заявки на замену;
- `notification-service` — уведомления, SSE, активность пользователей;
- `gateway-service` — единая точка входа.

Frontend:

- `teachsync-ui` — Angular-приложение, собранное и раздаваемое через Nginx.

## Полный запуск системы

```bash
docker compose up --build
```

После запуска:

- Frontend: http://localhost:4200
- API Gateway: http://localhost:8080
- Kafka UI: http://localhost:9090

## Запуск в фоне

```bash
docker compose up --build -d
```

Просмотр логов:

```bash
docker compose logs -f
```

Просмотр логов конкретного сервиса:

```bash
docker compose logs -f notification-service
```

Остановка:

```bash
docker compose down
```

Остановка с удалением данных PostgreSQL:

```bash
docker compose down -v
```

## Независимая сборка компонентов

Каждый сервис имеет собственный `Dockerfile`, поэтому его можно собрать отдельно.

```bash
docker compose build users-service
docker compose build course-service
docker compose build schedule-service
docker compose build replacement-service
docker compose build notification-service
docker compose build auth-service
docker compose build gateway-service
docker compose build teachsync-ui
```

## Независимый запуск отдельного сервиса

Пример: пересобрать и перезапустить только `notification-service`.

```bash
docker compose up --build notification-service
```

Пример: поднять только инфраструктуру.

```bash
docker compose up -d users-db course-db schedule-db replacement-db notification-db zookeeper kafka kafka-ui
```

Пример: поднять backend без frontend.

```bash
docker compose up --build auth-service users-service course-service schedule-service replacement-service notification-service gateway-service
```

## Межсервисные зависимости

В Docker-сети сервисы обращаются друг к другу по DNS-именам контейнеров:

- `http://users-service:8083`
- `http://course-service:8081`
- `http://schedule-service:8082`
- `http://replacement-service:8086`
- `http://notification-service:8085`
- `http://auth-service:8090`
- `http://gateway-service:8080`

Kafka внутри Docker доступна как:

```text
kafka:29092
```

Снаружи хоста Kafka доступна как:

```text
localhost:9092
```

## Для дипломного отчета

Можно описать, что система поддерживает независимое развертывание компонентов:

- каждый микросервис имеет собственный Dockerfile;
- каждый сервис использует отдельную базу данных;
- Kafka вынесена в отдельный инфраструктурный контейнер;
- Gateway маршрутизирует запросы к backend-сервисам;
- frontend собирается отдельно и раздается через Nginx;
- адреса межсервисного взаимодействия параметризованы через environment variables;
- общий запуск выполняется через `docker compose up --build`.

