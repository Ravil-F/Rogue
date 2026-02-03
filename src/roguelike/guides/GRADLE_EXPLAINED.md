# Объяснение build.gradle.kts для разработчиков на C/C++

Это руководство объясняет структуру и работу `build.gradle.kts` для тех, кто знаком с Makefile.

## Сравнение с Makefile

### Makefile (C/C++)
```makefile
CC := gcc
CFLAGS := -std=c11 -Wall
TARGET := app
SOURCES := $(shell find src -name '*.c')
OBJECTS := $(SOURCES:.c=.o)

$(TARGET): $(OBJECTS)
	$(CC) $(CFLAGS) -o $@ $(OBJECTS)
```

### build.gradle.kts (Java/Kotlin)
```kotlin
plugins { id("java") }
dependencies { implementation("library:name:version") }
tasks.jar { ... }
```

**Ключевое отличие:** 
- **Makefile** - декларативный, описывает **как** собирать
- **Gradle** - декларативный + императивный, описывает **что** нужно и **как** это сделать

---

## Структура build.gradle.kts

Разберем ваш файл построчно:

### 1. Плагины (plugins)

```kotlin
plugins {
    id("java")
}
```

**Аналог в Makefile:** Нет прямого аналога. Это как подключение библиотеки функций.

**Что делает:**
- Подключает плагин `java`, который добавляет задачи для компиляции Java
- Автоматически создает задачи: `compileJava`, `jar`, `test`, `clean` и др.

**Эквивалент в Makefile:**
```makefile
# В Makefile вы бы писали команды вручную:
compile:
	javac -d build/classes src/**/*.java
```

**В Gradle:** Плагин `java` уже знает, как компилировать Java, вам не нужно писать команды.

---

### 2. Метаданные проекта

```kotlin
group = "org.example"
version = "1.0-SNAPSHOT"
```

**Аналог в Makefile:**
```makefile
PROJECT_NAME := roguelike
VERSION := 1.0-SNAPSHOT
```

**Что делает:**
- `group` - идентификатор организации/группы (как namespace)
- `version` - версия проекта
- Используется для публикации артефактов и управления зависимостями

---

### 3. Репозитории (repositories)

```kotlin
repositories {
    mavenCentral()
}
```

**Аналог в Makefile:**
```makefile
# В Makefile вы бы загружали зависимости вручную:
download-deps:
	wget https://repo.example.com/library.jar -O libs/library.jar
```

**Что делает:**
- Указывает, откуда Gradle будет загружать зависимости
- `mavenCentral()` - центральный репозиторий Maven (как npm для JavaScript)
- Gradle автоматически скачивает зависимости при сборке

**Эквивалент:** Как если бы Makefile автоматически загружал все библиотеки из интернета.

---

### 4. Зависимости (dependencies)

```kotlin
dependencies {
    implementation("com.googlecode.lanterna:lanterna:3.2.0-alpha1")
    implementation("com.google.code.gson:gson:2.10.1")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}
```

**Аналог в Makefile:**
```makefile
# В Makefile вы бы указывали библиотеки вручную:
LIBS := -llanterna -lgson
CLASSPATH := libs/lanterna.jar:libs/gson.jar

download-deps:
	wget https://repo1.maven.org/.../lanterna.jar -O libs/lanterna.jar
	wget https://repo1.maven.org/.../gson.jar -O libs/gson.jar
```

**Что делает:**
- `implementation` - зависимости для компиляции и выполнения
- `testImplementation` - зависимости только для тестов
- Формат: `"группа:артефакт:версия"`

**Преимущество:** Gradle автоматически:
1. Скачивает зависимости
2. Разрешает конфликты версий
3. Загружает транзитивные зависимости (зависимости ваших зависимостей)

**В Makefile:** Пришлось бы делать все вручную.

---

### 5. Настройка задач (tasks)

```kotlin
tasks.test {
    useJUnitPlatform()
}
```

**Аналог в Makefile:**
```makefile
test: $(TEST_OBJECTS)
	java -cp $(CLASSPATH) org.junit.runner.JUnitCore TestSuite
```

**Что делает:**
- Настраивает встроенную задачу `test`
- `useJUnitPlatform()` - использует JUnit 5 для тестов
- Задача `test` уже создана плагином `java`

---

### 6. Настройка Java

```kotlin
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
```

**Аналог в Makefile:**
```makefile
JAVAC_FLAGS := -source 17 -target 17
```

**Что делает:**
- Указывает версию Java для исходного кода и целевого байт-кода
- Gradle использует эти настройки при компиляции

---

### 7. Настройка задачи jar

```kotlin
tasks.jar {
    archiveBaseName.set("roguelike")
    archiveVersion.set("")
    manifest {
        attributes(
            "Main-Class" to "Main",
            "Implementation-Title" to project.name,
            "Implementation-Version" to project.version
        )
    }
}
```

**Аналог в Makefile:**
```makefile
jar: $(CLASSES)
	echo "Main-Class: Main" > MANIFEST.MF
	jar cfm roguelike.jar MANIFEST.MF -C build/classes .
```

**Что делает:**
- Настраивает встроенную задачу `jar` (создание JAR файла)
- `archiveBaseName` - имя файла без расширения
- `archiveVersion` - версия в имени файла (пустая строка = без версии)
- `manifest` - создает MANIFEST.MF с метаданными
- `Main-Class` - указывает главный класс для запуска

**Результат:** `roguelike.jar` с указанным главным классом.

---

### 8. Создание пользовательской задачи (fatJar)

```kotlin
tasks.register<Jar>("fatJar") {
    archiveBaseName.set("roguelike")
    archiveVersion.set("")
    archiveClassifier.set("all")
    
    manifest {
        attributes(
            "Main-Class" to "Main",
            ...
        )
    }
    
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    with(tasks.jar.get() as CopySpec)
}
```

**Аналог в Makefile:**
```makefile
fat-jar: jar
	cp roguelike.jar roguelike-all.jar
	cd libs && for jar in *.jar; do jar xf $$jar; done
	jar uf roguelike-all.jar -C libs .
```

**Что делает:**
- `tasks.register<Jar>("fatJar")` - создает новую задачу типа `Jar`
- `archiveClassifier.set("all")` - добавляет суффикс `-all` к имени файла
- `from(...)` - добавляет содержимое зависимостей в JAR
  - `configurations.runtimeClasspath` - все зависимости для выполнения
  - `zipTree(it)` - распаковывает JAR-зависимости и добавляет их содержимое
- `duplicatesStrategy` - как обрабатывать дубликаты файлов
- `with(tasks.jar.get())` - включает содержимое обычного JAR

**Результат:** `roguelike-all.jar` со всеми зависимостями внутри.

---

### 9. Составные задачи (release, debug)

```kotlin
tasks.register("release") {
    dependsOn("clean", "fatJar")
    description = "Создает исполняемый JAR для распространения"
}
```

**Аналог в Makefile:**
```makefile
release: clean fat-jar
	@echo "Release build complete"

clean:
	rm -rf build/*

fat-jar: jar
	...
```

**Что делает:**
- `tasks.register("release")` - создает новую задачу
- `dependsOn("clean", "fatJar")` - указывает зависимости
  - Сначала выполнится `clean` (очистка)
  - Потом `fatJar` (создание fat JAR)
- `description` - описание задачи (показывается в `./gradlew tasks`)

**Выполнение:**
```bash
./gradlew release
# Эквивалентно:
# 1. ./gradlew clean
# 2. ./gradlew fatJar
```

---

## Концепции Gradle

### 1. Задачи (Tasks) - аналог целей в Makefile

**Makefile:**
```makefile
target: dependencies
	commands
```

**Gradle:**
```kotlin
tasks.register("target") {
    dependsOn("dependencies")
    doLast {
        // commands
    }
}
```

### 2. Зависимости (Dependencies)

**Makefile:**
```makefile
app: main.o utils.o
	$(CC) -o app main.o utils.o
```

**Gradle:**
```kotlin
tasks.register("build") {
    dependsOn("compileJava", "processResources")
}
```

### 3. Конфигурации

**Makefile:** Переменные
```makefile
CC := gcc
CFLAGS := -Wall
```

**Gradle:** Свойства и конфигурации
```kotlin
java {
    sourceCompatibility = JavaVersion.VERSION_17
}
```

---

## Жизненный цикл сборки

### Makefile:
```makefile
all: clean compile link
```

### Gradle:
```bash
./gradlew build
```

**Что происходит:**
1. **Инициализация** - загрузка настроек проекта
2. **Конфигурация** - выполнение build.gradle.kts, создание графа задач
3. **Выполнение** - выполнение задач в правильном порядке

**Аналог в Makefile:**
```makefile
# Фаза конфигурации (выполняется всегда)
SOURCES := $(shell find src -name '*.c')

# Фаза выполнения (только при вызове)
compile: $(SOURCES)
	$(CC) -c $< -o $@
```

---

## Полезные команды Gradle

### Просмотр задач
```bash
./gradlew tasks          # Все задачи
./gradlew tasks --all    # Все задачи включая скрытые
```

**Аналог в Makefile:**
```makefile
help:
	@echo "Available targets:"
	@echo "  compile - Compile sources"
	@echo "  clean   - Clean build"
```

### Выполнение задач
```bash
./gradlew clean          # Очистка
./gradlew build          # Сборка
./gradlew test           # Тесты
./gradlew release        # Release сборка
```

**Аналог в Makefile:**
```bash
make clean
make build
make test
make release
```

### Отладка
```bash
./gradlew build --info      # Подробный вывод
./gradlew build --debug     # Очень подробный вывод
./gradlew build --dry-run   # Показать что будет выполнено
```

---

## Расширенные возможности

### Условная логика

**Makefile:**
```makefile
ifeq ($(CONFIG),release)
    CFLAGS += -O3
else
    CFLAGS += -g
endif
```

**Gradle:**
```kotlin
if (project.hasProperty("release")) {
    tasks.jar {
        // настройки для release
    }
} else {
    tasks.jar {
        // настройки для debug
    }
}
```

### Переменные окружения

**Makefile:**
```makefile
JAVA_HOME := $(shell echo $$JAVA_HOME)
```

**Gradle:**
```kotlin
val javaHome = System.getenv("JAVA_HOME")
```

### Многомодульные проекты

**Makefile:** Сложно, нужно писать для каждого модуля отдельно

**Gradle:** Автоматически через `settings.gradle.kts`:
```kotlin
include("module1", "module2")
```

---

## Сравнительная таблица

| Концепция | Makefile | Gradle |
|-----------|----------|--------|
| **Задачи** | `target: deps` | `tasks.register("target")` |
| **Зависимости** | `target: dep1 dep2` | `dependsOn("dep1", "dep2")` |
| **Переменные** | `VAR := value` | `val var = value` |
| **Условия** | `ifeq/ifdef` | `if/else` |
| **Функции** | `$(shell cmd)` | `doLast { ... }` |
| **Автоматизация** | Ручная | Встроенная |
| **Управление зависимостями** | Ручное | Автоматическое |
| **Кэширование** | Ручное | Автоматическое |
| **Параллельная сборка** | `make -j4` | `--parallel` |

---

## Практические примеры

### Пример 1: Простая задача

**Makefile:**
```makefile
hello:
	@echo "Hello, World!"
```

**Gradle:**
```kotlin
tasks.register("hello") {
    doLast {
        println("Hello, World!")
    }
}
```

### Пример 2: Задача с зависимостями

**Makefile:**
```makefile
build: compile link
	@echo "Build complete"

compile:
	javac src/*.java

link:
	jar cf app.jar -C classes .
```

**Gradle:**
```kotlin
tasks.register("build") {
    dependsOn("compileJava", "jar")
    doLast {
        println("Build complete")
    }
}
// compileJava и jar уже определены плагином java
```

### Пример 3: Условная сборка

**Makefile:**
```makefile
ifdef DEBUG
    CFLAGS += -g -DDEBUG
endif
```

**Gradle:**
```kotlin
tasks.compileJava {
    if (project.hasProperty("debug")) {
        options.compilerArgs.add("-g")
        options.compilerArgs.add("-DDEBUG")
    }
}
```

---

## Преимущества Gradle перед Makefile

1. **Автоматическое управление зависимостями**
   - Не нужно вручную скачивать библиотеки
   - Автоматическое разрешение конфликтов версий

2. **Кэширование**
   - Gradle кэширует результаты задач
   - Повторная сборка быстрее

3. **Инкрементальная сборка**
   - Пересобирает только измененные файлы
   - Аналог: `make` делает то же самое, но Gradle умнее

4. **Плагины**
   - Готовые решения для разных задач
   - Не нужно писать команды вручную

5. **Кроссплатформенность**
   - Один файл работает на всех ОС
   - В Makefile нужно учитывать различия

---

## Недостатки Gradle

1. **Сложность для простых проектов**
   - Для простого проекта Makefile может быть проще

2. **Требует Java**
   - Gradle написан на Java/Kotlin
   - Makefile работает везде

3. **Кривая обучения**
   - Больше концепций, чем в Makefile
   - Но более мощный инструмент

---

## Резюме

**build.gradle.kts** - это декларативный файл конфигурации, который:

1. **Описывает проект** (метаданные, зависимости)
2. **Настраивает задачи** (компиляция, упаковка, тесты)
3. **Определяет зависимости** между задачами
4. **Автоматизирует** рутинные операции

**Основное отличие от Makefile:**
- Makefile: вы пишете **команды** для сборки
- Gradle: вы **описываете** что нужно сделать, Gradle сам знает как

**Аналогия:**
- Makefile = инструкция "как собрать мебель" (шаг за шагом)
- Gradle = описание "какая мебель нужна" (система сама знает как собрать)

---

## Дополнительные ресурсы

- [Gradle User Guide](https://docs.gradle.org/current/userguide/userguide.html)
- [Gradle Build Script Basics](https://docs.gradle.org/current/userguide/tutorial_using_tasks.html)
- [Gradle Kotlin DSL](https://docs.gradle.org/current/userguide/kotlin_dsl.html)
