# Как Gradle находит файлы для сборки

## Концепция: Convention over Configuration

**Ключевое отличие от Makefile:**

- **Makefile**: Вы **явно указываете** все файлы
- **Gradle**: Использует **конвенции** (стандартные правила) и **автоматически находит** файлы

---

## Сравнение с Makefile

### Makefile (явное указание файлов)

```makefile
# Вы должны явно указать все файлы
SOURCES := file1.c folder1/file2.c file3.c
OBJECTS := $(SOURCES:.c=.o)

app: $(OBJECTS)
	gcc -o app $(OBJECTS)
```

**Проблема:** Если добавите новый файл, нужно обновить Makefile.

### Gradle (автоматическое обнаружение)

```kotlin
plugins { id("java") }
// Всё! Gradle сам найдет все .java файлы
```

**Преимущество:** Добавили файл? Gradle автоматически его найдет.

---

## Стандартная структура проекта Gradle

Gradle использует **конвенции** - стандартную структуру директорий:

```
src/
├── main/              # Основной код приложения
│   ├── java/         # Java исходные файлы
│   │   └── ...       # Все .java файлы здесь
│   └── resources/   # Ресурсы (properties, XML, и т.д.)
│       └── ...
└── test/             # Тестовый код
    ├── java/        # Тестовые .java файлы
    └── resources/   # Ресурсы для тестов
```

### Ваш проект

```
src/roguelike/
├── src/
│   ├── main/
│   │   ├── java/              ← Gradle автоматически найдет ВСЕ .java файлы здесь
│   │   │   ├── Main.java
│   │   │   ├── domain/
│   │   │   │   ├── Model.java
│   │   │   │   ├── player/
│   │   │   │   │   └── Player.java
│   │   │   │   └── ...
│   │   │   ├── presentation/
│   │   │   │   └── View.java
│   │   │   └── utils/
│   │   │       └── ...
│   │   └── resources/         ← Gradle автоматически включит ВСЕ файлы здесь
│   │       ├── common.properties
│   │       ├── enemy.properties
│   │       └── player.properties
│   └── test/                 ← Gradle автоматически найдет тесты здесь
│       └── java/
```

**Gradle автоматически:**
1. Найдет все `.java` файлы в `src/main/java/` (включая подпапки)
2. Включит все файлы из `src/main/resources/` в JAR
3. Найдет все тесты в `src/test/java/`

---

## Source Sets (Наборы исходников)

Gradle использует концепцию **Source Sets** - наборы исходных файлов.

### Основные Source Sets

1. **`main`** - основной код приложения
   - Java файлы: `src/main/java/`
   - Ресурсы: `src/main/resources/`

2. **`test`** - тестовый код
   - Java файлы: `src/test/java/`
   - Ресурсы: `src/test/resources/`

### Как это работает

Когда вы запускаете `./gradlew build`, Gradle:

1. **Сканирует** `src/main/java/` рекурсивно
2. **Находит** все файлы с расширением `.java`
3. **Компилирует** их в байт-код
4. **Копирует** файлы из `src/main/resources/` в JAR

**Вам не нужно ничего указывать!**

---

## Что происходит под капотом

### Эквивалент в Makefile

Если бы вы писали Makefile для вашего проекта:

```makefile
# Пришлось бы явно указать все файлы
JAVA_SOURCES := \
    src/main/java/Main.java \
    src/main/java/domain/Model.java \
    src/main/java/domain/player/Player.java \
    src/main/java/presentation/View.java \
    src/main/java/utils/SaveGame.java \
    # ... и еще 40+ файлов

# Или использовать find
JAVA_SOURCES := $(shell find src/main/java -name "*.java")

compile:
	javac -d build/classes $(JAVA_SOURCES)
```

**Проблемы:**
- Нужно обновлять список при добавлении файлов
- Легко забыть добавить новый файл
- Нужно вручную управлять зависимостями

### В Gradle

```kotlin
plugins { id("java") }
// Всё! Gradle сам:
// 1. Найдет все .java файлы в src/main/java/
// 2. Определит зависимости между классами
// 3. Скомпилирует в правильном порядке
// 4. Включит ресурсы из src/main/resources/
```

---

## Как Gradle находит файлы: пошагово

### Шаг 1: Плагин Java определяет Source Sets

Когда вы пишете:
```kotlin
plugins { id("java") }
```

Плагин автоматически создает:
- Source Set `main` → `src/main/java/` и `src/main/resources/`
- Source Set `test` → `src/test/java/` и `src/test/resources/`

### Шаг 2: Задача compileJava сканирует директории

Задача `compileJava` (создается плагином) автоматически:
```kotlin
// Псевдокод того, что делает Gradle
sourceSets.main.java.srcDirs = ["src/main/java"]
sourceSets.main.resources.srcDirs = ["src/main/resources"]

// Gradle рекурсивно находит все файлы
val javaFiles = fileTree("src/main/java").include("**/*.java")
val resourceFiles = fileTree("src/main/resources")
```

### Шаг 3: Компиляция

Gradle компилирует все найденные `.java` файлы:
```bash
javac -d build/classes/java/main \
    src/main/java/Main.java \
    src/main/java/domain/Model.java \
    src/main/java/domain/player/Player.java \
    # ... все остальные файлы автоматически
```

---

## Просмотр того, что нашел Gradle

### Команда для просмотра Source Sets

```bash
./gradlew sourceSets
```

Вывод покажет:
```
main
  Compile classpath: ...
  Java sources: [src/main/java]
  Resources: [src/main/resources]

test
  Compile classpath: ...
  Java sources: [src/test/java]
  Resources: [src/test/resources]
```

### Команда для просмотра файлов

```bash
./gradlew compileJava --info
```

Покажет все файлы, которые будут скомпилированы.

---

## Настройка (если нужно изменить стандартную структуру)

### Пример 1: Дополнительные директории с исходниками

```kotlin
java {
    sourceSets {
        main {
            java {
                // Добавить еще одну директорию
                srcDir("src/custom/java")
            }
        }
    }
}
```

Теперь Gradle будет искать файлы в:
- `src/main/java/` (стандартная)
- `src/custom/java/` (дополнительная)

### Пример 2: Изменить стандартную структуру

```kotlin
java {
    sourceSets {
        main {
            java {
                // Полностью изменить путь
                setSrcDirs(listOf("custom/path/to/java"))
            }
            resources {
                setSrcDirs(listOf("custom/path/to/resources"))
            }
        }
    }
}
```

### Пример 3: Исключить определенные файлы

```kotlin
tasks.compileJava {
    exclude("**/OldClass.java")
    exclude("**/legacy/**")
}
```

---

## Сравнение: Makefile vs Gradle

### Makefile

```makefile
# Явное указание всех файлов
SOURCES := \
    src/main.c \
    src/utils.c \
    src/domain/model.c

# Или использование find
SOURCES := $(shell find src -name "*.c")

# Проблемы:
# - Нужно обновлять при добавлении файлов
# - Легко забыть файл
# - Нет автоматического управления зависимостями
```

### Gradle

```kotlin
plugins { id("java") }

// Gradle автоматически:
// - Находит все .java файлы в src/main/java/
// - Определяет зависимости
// - Компилирует в правильном порядке
// - Включает ресурсы
```

---

## Как работает задача jar

Когда вы запускаете `./gradlew jar`, задача `jar`:

1. **Берет скомпилированные классы** из `build/classes/java/main/`
2. **Берет ресурсы** из `src/main/resources/`
3. **Создает MANIFEST.MF** с указанными атрибутами
4. **Упаковывает все в JAR**

```kotlin
tasks.jar {
    // Gradle автоматически включает:
    // - Все .class файлы из build/classes/java/main/
    // - Все файлы из src/main/resources/
    
    // Вы настраиваете только метаданные:
    manifest {
        attributes("Main-Class" to "Main")
    }
}
```

---

## Как работает задача fatJar

```kotlin
tasks.register<Jar>("fatJar") {
    // 1. Берет содержимое обычного JAR (классы + ресурсы)
    with(tasks.jar.get() as CopySpec)
    
    // 2. Добавляет все зависимости
    from(configurations.runtimeClasspath.get().map { 
        if (it.isDirectory) it else zipTree(it) 
    })
}
```

**Что происходит:**
1. Gradle берет все JAR-зависимости из `configurations.runtimeClasspath`
2. Распаковывает их (`zipTree`)
3. Добавляет содержимое в fat JAR
4. Исключает дубликаты (`duplicatesStrategy`)

---

## Практические примеры

### Пример 1: Добавление нового файла

**В Makefile:**
```makefile
# Нужно обновить список
SOURCES := \
    file1.c \
    file2.c \
    newfile.c  # ← добавили вручную
```

**В Gradle:**
```bash
# Просто создайте файл
touch src/main/java/NewClass.java

# Gradle автоматически найдет его при следующей сборке
./gradlew build
```

### Пример 2: Многомодульный проект

**В Makefile:**
```makefile
# Нужно писать отдельные правила для каждого модуля
MODULE1_SOURCES := module1/src/*.c
MODULE2_SOURCES := module2/src/*.c
```

**В Gradle:**
```kotlin
// settings.gradle.kts
include("module1", "module2")

// Gradle автоматически найдет файлы в каждом модуле
// по стандартной структуре
```

---

## Резюме

### Как Gradle находит файлы:

1. **Конвенции** - использует стандартную структуру:
   - `src/main/java/` → исходный код
   - `src/main/resources/` → ресурсы
   - `src/test/java/` → тесты

2. **Рекурсивный поиск** - автоматически находит все файлы в подпапках

3. **Source Sets** - группирует файлы по назначению (main, test)

4. **Автоматическое управление** - не нужно обновлять списки файлов

### Преимущества перед Makefile:

- ✅ Не нужно явно указывать файлы
- ✅ Автоматическое обнаружение новых файлов
- ✅ Автоматическое определение зависимостей
- ✅ Стандартная структура для всех проектов
- ✅ Легко настроить, если нужно

### Когда нужно настраивать:

- Нестандартная структура проекта
- Дополнительные директории с исходниками
- Исключение определенных файлов
- Специальные требования к сборке

**В 99% случаев** стандартной структуры достаточно, и Gradle все сделает автоматически!
