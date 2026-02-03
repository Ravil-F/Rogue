# Руководство по сборке Java-проекта roguelike

Это руководство описывает сборку Java-проекта через Gradle и создание исполняемого JAR файла для распространения.

## Содержание

1. [Способы сборки](#способы-сборки)
2. [Сборка через Gradle](#сборка-через-gradle)
3. [Запуск приложения](#запуск-приложения)

---

## Способы сборки

Проект использует Gradle для сборки и создания JAR файлов:

### **JAR файлы** (Java Archive)
- Стандартный способ распространения Java-приложений
- Требует установленную JRE/JDK на целевой машине
- Запуск: `java -jar app.jar`

### **Fat JAR / Uber JAR**
- JAR файл со всеми зависимостями внутри
- Удобен для распространения (один файл)
- Все еще требует JRE
- Создается командой `./gradlew release`

---

## Сборка через Gradle

Gradle уже настроен в проекте. Это самый простой способ для разработки.

### Требования
- Java JDK 17 или выше
- Gradle (или используйте `gradlew` из проекта)

### Команды

```bash
# Перейти в директорию проекта
cd src/roguelike

# Сборка release версии (создает fat JAR)
./gradlew release
# Или на Windows:
gradlew.bat release

# Сборка debug версии
./gradlew debug

# Обычная сборка
./gradlew build

# Запуск приложения
java -jar build/libs/roguelike-all.jar
```

### Результаты сборки

- **Release**: `build/libs/roguelike-all.jar` - исполняемый JAR со всеми зависимостями
- **Debug**: `build/libs/roguelike.jar` - обычный JAR (без зависимостей)

### Преимущества Gradle
- ✅ Автоматическое управление зависимостями
- ✅ Кроссплатформенность
- ✅ Интеграция с IDE
- ✅ Поддержка тестов, плагинов и расширений

---

## Запуск приложения

После сборки проекта вы можете запустить игру несколькими способами:

### Способ 1: Из командной строки (рекомендуется)

```bash
cd src/roguelike

# Запуск из текущей директории
java -jar build/libs/roguelike-all.jar

# Или с полным путем
java -jar /путь/к/проекту/src/roguelike/build/libs/roguelike-all.jar
```

### Способ 2: Двойной клик (Windows/Linux с GUI)

Просто дважды кликните на файл `build/libs/roguelike-all.jar` в проводнике файлов.

**Примечание:** Для работы двойного клика на Linux может потребоваться настройка ассоциации файлов с Java.

### Способ 3: Создание скрипта запуска

#### Linux/macOS:
```bash
# Создать скрипт run.sh
echo '#!/bin/bash
cd "$(dirname "$0")"
java -jar build/libs/roguelike-all.jar' > run.sh
chmod +x run.sh

# Запуск
./run.sh
```

#### Windows:
```batch
@echo off
cd /d "%~dp0"
java -jar build\libs\roguelike-all.jar
pause
```
Сохраните как `run.bat` в директории проекта.

### Требования для запуска

- **Java Runtime Environment (JRE) 17 или выше** должна быть установлена
- Проверьте установку: `java -version`
- Если Java не установлена, скачайте с [Oracle](https://www.oracle.com/java/technologies/downloads/) или используйте [OpenJDK](https://openjdk.org/)

---

## Рекомендации

### Для разработки
- Используйте **Gradle** (`./gradlew build`)
- Быстрая сборка, интеграция с IDE

### Для распространения
- Используйте **Fat JAR** (`./gradlew release`)
- Один файл, легко запускать
- Запуск: `java -jar build/libs/roguelike-all.jar`

---

## Дополнительные ресурсы

- [Gradle User Guide](https://docs.gradle.org/)
- [Java Packaging Guide](https://docs.oracle.com/javase/17/docs/specs/jar/jar.html)

---

## Примеры использования

### Быстрый старт
```bash
cd src/roguelike
./gradlew release
java -jar build/libs/roguelike-all.jar
```
