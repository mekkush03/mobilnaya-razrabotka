# DailyNotifications

## Материалы
- [Презентация](media/presentation.pptx)
- [Видео](media/reminders.mp4)

DailyNotifications — Android-приложение для создания напоминаний и получения локальных уведомлений.

## Возможности
- Создание, редактирование и удаление напоминаний.
- Частота: разово, ежедневно, еженедельно, пользовательская.
- Список с группировкой по датам и разделением на будущие/прошедшие.
- Локальные уведомления через WorkManager.
- Профиль: вход/регистрация или гостевой режим.
- Настройки: включение уведомлений и выбор формата времени (12/24).

## Диаграммы и экраны
![Диаграмма использования](images/use-case-diagram.png)
![Экран входа](images/login.png)
![Создание напоминания](images/create-reminder.png)
![Список напоминаний](images/reminder-list.png)
![Уведомление о напоминании](images/notify-reminder.png)

## Технологии
- Kotlin, Jetpack Compose (Material 3)
- Navigation Compose
- WorkManager
- Room (SQLite)
- Hilt (DI)
- Ktor Client / Ktor Server

## Требования
- Android Studio
- JDK 11
- minSdk 24, targetSdk 36

## Запуск
1. Откройте проект в Android Studio.
2. Дождитесь синхронизации Gradle.
3. Запустите бэкенд: `.\gradlew.bat :backend:bootRun`
4. Запустите конфигурацию `app` на эмуляторе или устройстве.

По умолчанию Android-клиент ходит в `http://10.0.2.2:8080`, что подходит для Android Emulator. Для физического устройства нужно заменить `BACKEND_URL` в [app/build.gradle.kts](/C:/Users/venya/AndroidStudioProjects/DailyNotifications/app/build.gradle.kts:22) на IP машины в локальной сети.

## Сборка из командной строки
```powershell
.\gradlew assembleDebug
```

```powershell
.\gradlew :backend:bootRun
```

## Разрешения
- `POST_NOTIFICATIONS` требуется на Android 13+.

## Примечания
- Бэкенд переписан в Spring Boot-архитектуре по образцу `controller/service/repository/entity/dto/security/config/exception`.
- По умолчанию сервер хранит данные в файловой H2 базе внутри `backend/build/db`, поэтому данные переживают обычный перезапуск процесса.
- Авторизация в приложении использует сервер, а токен сохраняется локально в `SharedPreferences`.
- Настройки по-прежнему хранятся в памяти и сбрасываются после перезапуска приложения.
