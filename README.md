# PCB Dispatcher API

## Описание

PCB Dispatcher API — это RESTful API для управления перемещением печатных плат по этапам производства. API предоставляет возможности для регистрации новых плат, перемещения их на следующий этап, отправки в ремонт и получения истории перемещений.

## Технологии и особенности реализации

### Основной стек:
- Java 21
- Spring Boot 3.3.6
- Spring Data JPA
- H2 Database (in-memory)
- OpenAPI/Swagger для документации API
- Lombok для уменьшения шаблонного кода

### Тестирование:
- JUnit 5 для модульных тестов
- Mockito для мокирования в тестах
- Spring Boot Test для интеграционных тестов
- JaCoCo для анализа покрытия кода (минимальный порог: 85%)

### Архитектурные особенности:
- RESTful API с версионированием (v1)
- Слоистая архитектура (контроллеры, сервисы, репозитории)
- DTO для разделения слоя API и бизнес-логики
- Обработка ошибок через глобальный exception handler
- Валидация входных данных с помощью Jakarta Validation

### Документация:
- Swagger UI с подробным описанием всех эндпоинтов
- Примеры запросов и ответов в OpenAPI спецификации
- Аннотации для описания API операций и моделей

### Мониторинг и логирование:
- Логирование с использованием SLF4J
- Настроенная ротация логов
- Настроенные уровни логирования для разных пакетов

## Установка и запуск

### Предварительные требования

- Java 21
- Maven
- PostgreSQL (или H2 для разработки)

### Шаги установки

1. **Клонируйте репозиторий:**

   ```bash
   git clone https://github.com/2duserAR/pcb_dispatcher_v1.git
   cd pcb_dispatcher_v1
   ```

2. **Настройка базы данных:**

   По умолчанию приложение использует встроенную базу данных H2 со следующими параметрами:
   ```yaml
   spring:
     datasource:
       url: jdbc:h2:mem:pcbdb
       username: sa
       password: password
   ```
   
   Для использования PostgreSQL:
   1. Создайте базу данных PostgreSQL
   2. Установите переменные окружения:
      ```bash
      export DB_USERNAME=your_username
      export DB_PASSWORD=your_password
      ```
   3. Запустите приложение с prod профилем:
      ```bash
      mvn spring-boot:run -Dspring.profiles.active=prod
      ```

3. **Соберите и запустите приложение:**

   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

4. **Доступ к API:**

   Базовый путь API: `http://localhost:8080/api/v1`

   Доступные эндпоинты:
   1. `POST /pcbs` - создание новой платы
   2. `GET /pcbs` - получение списка всех плат
   3. `POST /pcbs/{id}/next` - перемещение платы на следующий этап
   4. `POST /pcbs/{id}/repair` - отправка платы в ремонт
   5. `GET /pcbs/{id}/history` - получение истории перемещений платы

5. **Документация API:**

   Swagger UI доступен по адресу `http://localhost:8080/swagger-ui.html`.

## Использование

### Примеры запросов

- **Создание новой платы:**

  ```http
  POST /api/v1/pcbs
  Content-Type: application/json

  {
    "serialNumber": "TEST001"
  }
  ```

  Серийный номер должен соответствовать формату `TESTxxx`, где:
  - `TEST` - префикс (заглавными буквами)
  - `xxx` - три цифры (например: 001, 002, ..., 999)

  Примеры корректных серийных номеров:
  - TEST001
  - TEST042
  - TEST999

- **Перемещение платы на следующий этап:**

  ```http
  POST /api/v1/pcbs/{id}/next
  ```

- **Отправка платы в ремонт:**

  ```http
  POST /api/v1/pcbs/{id}/repair
  ```

- **Получение истории перемещений:**

  ```http
  GET /api/v1/pcbs/{id}/history
  ```

### Профили приложения

Приложение поддерживает три профиля, конфигурация которых описана в application.yml:

1. **dev** (по умолчанию):
   ```yaml
   spring:
     h2:
       console:
         enabled: true
     jpa:
       show-sql: true
   logging:
     level:
       root: DEBUG
   ```

2. **test**:
   ```yaml
   spring:
     h2:
       console:
         enabled: false
     jpa:
       show-sql: false
   logging:
     level:
       root: WARN
   ```

3. **prod**:
   ```yaml
   spring:
     datasource:
       url: jdbc:postgresql://localhost:5432/pcbdb
       username: ${DB_USERNAME}
       password: ${DB_PASSWORD}
     jpa:
       show-sql: false
     h2:
       console:
         enabled: false
   logging:
     level:
       root: INFO
   ```

Для запуска приложения с определенным профилем используйте:
```bash
# Запуск с профилем разработки
mvn spring-boot:run -Dspring.profiles.active=dev

# Запуск с тестовым профилем
mvn spring-boot:run -Dspring.profiles.active=test

# Запуск с продакшен профилем
mvn spring-boot:run -Dspring.profiles.active=prod
```

Для продакшен окружения необходимо установить следующие переменные окружения:
- DB_USERNAME - имя пользователя базы данных
- DB_PASSWORD - пароль базы данных