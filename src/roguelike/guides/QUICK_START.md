# Быстрый старт - Сборка проекта

## ✅ Gradle

```bash
cd src/roguelike
./gradlew release
java -jar build/libs/roguelike-all.jar
```

**Если получаете ошибку:** `Could not find or load main class org.gradle.wrapper.GradleWrapperMain`

Это означает, что `gradle-wrapper.jar` поврежден. Исправьте это:

```bash
# Автоматическое исправление
chmod +x fix-gradle-wrapper.sh
./fix-gradle-wrapper.sh

# Или вручную
wget https://raw.githubusercontent.com/gradle/gradle/v8.8.0/gradle/wrapper/gradle-wrapper.jar -O gradle/wrapper/gradle-wrapper.jar
chmod +x gradlew
```

### Запуск игры из консоли

После сборки запустите игру командой:

```bash
java -jar build/libs/roguelike-all.jar
```

Или двойным кликом на файл `build/libs/roguelike-all.jar` в проводнике.