# Исправление проблемы с Gradle Wrapper

## Проблема
Ошибка: `Could not find or load main class org.gradle.wrapper.GradleWrapperMain`

**Причина:** файл `gradle-wrapper.jar` имеет размер всего 130 байт вместо ~60-70 КБ. Это означает, что файл поврежден или это указатель Git LFS, который не был загружен.

## ✅ Решение 1: Автоматический скрипт (рекомендуется)

В WSL выполните:

```bash
cd src/roguelike
chmod +x fix-gradle-wrapper.sh
./fix-gradle-wrapper.sh
```

Скрипт автоматически загрузит правильный `gradle-wrapper.jar`.

## Решение 2: Ручная загрузка

### В WSL (Ubuntu):

```bash
cd src/roguelike

# Удалить поврежденный файл
rm -f gradle/wrapper/gradle-wrapper.jar

# Скачать правильный файл
wget https://raw.githubusercontent.com/gradle/gradle/v8.8.0/gradle/wrapper/gradle-wrapper.jar -O gradle/wrapper/gradle-wrapper.jar

# Или через curl:
# curl -L https://raw.githubusercontent.com/gradle/gradle/v8.8.0/gradle/wrapper/gradle-wrapper.jar -o gradle/wrapper/gradle-wrapper.jar

# Установить права
chmod +x gradlew

# Проверка
ls -lh gradle/wrapper/gradle-wrapper.jar
# Должен показать размер ~60-70 КБ
```

## Решение 3: Использовать системный Gradle

Если у вас установлен Gradle в системе:

```bash
cd src/roguelike

# Пересоздать wrapper
gradle wrapper --gradle-version 8.8

# Теперь можно использовать
./gradlew release
```

## Проверка после исправления

```bash
cd src/roguelike

# Проверить размер файла
ls -lh gradle/wrapper/gradle-wrapper.jar
# Должен показать размер ~60-70 КБ

# Проверить версию Gradle
./gradlew --version

# Попробовать собрать проект
./gradlew release

# Запустить игру
java -jar build/libs/roguelike-all.jar
```
