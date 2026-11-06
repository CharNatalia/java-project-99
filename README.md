# Менеджер задач (Java)

[![CI](https://github.com/CharNatalia/java-project-99/actions/workflows/ci.yml/badge.svg)](https://github.com/CharNatalia/java-project-99/actions/workflows/ci.yml)
[![Quality gate status](https://sonarcloud.io/api/project_badges/measure?project=CharNatalia_java-project-99&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=CharNatalia_java-project-99)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=CharNatalia_java-project-99&metric=coverage)](https://sonarcloud.io/summary/new_code?id=CharNatalia_java-project-99)
[![hexlet-check](https://github.com/CharNatalia/java-project-99/actions/workflows/hexlet-check.yml/badge.svg)](https://github.com/CharNatalia/java-project-99/actions)

**[🌐 Менеджер задач](https://java-project-99-c15r.onrender.com)** — система управления задачами, подобная Redmine. Она позволяет создавать задачи, назначать исполнителей и менять статусы. Для работы с системой требуется аутентификация.

## Стек

- **Java 21**, **Spring Boot 4** — серверная часть приложения.
- **Spring Web MVC** — REST API.
- **Spring Data JPA / Hibernate** — работа с базой данных.
- **PostgreSQL** — база данных в production; **H2** — для локальной разработки.
- **Spring Security**, **JWT** — аутентификация и авторизация.
- **Bean Validation** — валидация входных данных.
- **MapStruct** — преобразование DTO.
- **Lombok** — сокращение шаблонного кода.
- **Gradle** — сборка проекта и управление зависимостями.
- **JUnit 5**, **Spring Boot Test**, **AssertJ**, **JSON Unit**, **Instancio** — тестирование.
- **JaCoCo**, **Checkstyle**, **SonarCloud** — покрытие тестами и контроль качества кода.
- **GitHub Actions** — непрерывная интеграция.
- **Docker**, **Render** — контейнеризация и деплой.
- **Bugsink**, **Sentry SDK** — отслеживание ошибок.
- **Swagger** - документация.

## Установка и локальный запуск

Для запуска понадобятся JDK 21 и Git.

Склонируйте репозиторий и перейдите в папку приложения:

```bash
git clone https://github.com/CharNatalia/java-project-99.git
cd java-project-99/app
```

Запустите приложение:

```bash
./gradlew bootRun
```

Откройте приложение: [http://localhost:8080](http://localhost:8080).

По умолчанию используется база H2 в памяти. Схема создаётся автоматически при запуске, данные удаляются после остановки приложения.

## Использование

1. Откройте приложение и войдите в систему.
2. Создайте пользователей, которым будут назначаться задачи.
3. Добавьте статусы задач и метки.
4. Создайте задачу.
5. Используйте фильтры для поиска задач по названию, исполнителю, статусу и меткам.
6. Редактируйте задачи и меняйте их статусы по мере выполнения.

Документация REST API доступна в [Swagger UI на Render](https://java-project-99-c15r.onrender.com/swagger-ui/index.html).
