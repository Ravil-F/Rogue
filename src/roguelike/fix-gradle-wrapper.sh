#!/bin/bash
# Скрипт для исправления Gradle Wrapper
# Использование: ./fix-gradle-wrapper.sh

echo "=== Исправление Gradle Wrapper ==="
echo ""

# Проверка текущей директории
if [ ! -f "build.gradle.kts" ]; then
    echo "Ошибка: запустите скрипт из директории src/roguelike"
    exit 1
fi

# Проверка размера текущего файла
CURRENT_SIZE=$(stat -f%z gradle/wrapper/gradle-wrapper.jar 2>/dev/null || stat -c%s gradle/wrapper/gradle-wrapper.jar 2>/dev/null || echo "0")

if [ "$CURRENT_SIZE" -gt 50000 ]; then
    echo "✓ gradle-wrapper.jar уже имеет правильный размер ($CURRENT_SIZE байт)"
    echo "Проблема может быть в другом месте."
    exit 0
fi

echo "Текущий размер gradle-wrapper.jar: $CURRENT_SIZE байт (должно быть ~60-70 КБ)"
echo ""

# Вариант 1: Скачать напрямую
echo "Вариант 1: Загрузка gradle-wrapper.jar напрямую..."
if command -v wget &> /dev/null; then
    wget -q https://raw.githubusercontent.com/gradle/gradle/v8.8.0/gradle/wrapper/gradle-wrapper.jar -O gradle/wrapper/gradle-wrapper.jar.tmp
    if [ $? -eq 0 ] && [ -f gradle/wrapper/gradle-wrapper.jar.tmp ]; then
        mv gradle/wrapper/gradle-wrapper.jar.tmp gradle/wrapper/gradle-wrapper.jar
        NEW_SIZE=$(stat -f%z gradle/wrapper/gradle-wrapper.jar 2>/dev/null || stat -c%s gradle/wrapper/gradle-wrapper.jar 2>/dev/null)
        echo "✓ Файл загружен. Новый размер: $NEW_SIZE байт"
    else
        echo "✗ Не удалось загрузить через wget"
        rm -f gradle/wrapper/gradle-wrapper.jar.tmp
    fi
elif command -v curl &> /dev/null; then
    curl -sL https://raw.githubusercontent.com/gradle/gradle/v8.8.0/gradle/wrapper/gradle-wrapper.jar -o gradle/wrapper/gradle-wrapper.jar.tmp
    if [ $? -eq 0 ] && [ -f gradle/wrapper/gradle-wrapper.jar.tmp ]; then
        mv gradle/wrapper/gradle-wrapper.jar.tmp gradle/wrapper/gradle-wrapper.jar
        NEW_SIZE=$(stat -f%z gradle/wrapper/gradle-wrapper.jar 2>/dev/null || stat -c%s gradle/wrapper/gradle-wrapper.jar 2>/dev/null)
        echo "✓ Файл загружен. Новый размер: $NEW_SIZE байт"
    else
        echo "✗ Не удалось загрузить через curl"
        rm -f gradle/wrapper/gradle-wrapper.jar.tmp
    fi
else
    echo "✗ Не найдены wget или curl"
fi

# Проверка результата
FINAL_SIZE=$(stat -f%z gradle/wrapper/gradle-wrapper.jar 2>/dev/null || stat -c%s gradle/wrapper/gradle-wrapper.jar 2>/dev/null || echo "0")

if [ "$FINAL_SIZE" -lt 50000 ]; then
    echo ""
    echo "⚠ Не удалось загрузить файл автоматически."
    echo ""
    echo "Вариант 2: Используйте системный Gradle (если установлен):"
    echo "  gradle wrapper --gradle-version 8.8"
    echo ""
    echo "Вариант 3: Используйте Makefile (не требует Gradle):"
    echo "  make release"
    exit 1
fi

# Установка прав на выполнение
chmod +x gradlew 2>/dev/null || true

echo ""
echo "=== Проверка ==="
echo "Размер файла: $FINAL_SIZE байт"
echo ""

# Попытка проверить версию
if [ -f gradlew ]; then
    echo "Проверка Gradle Wrapper..."
    ./gradlew --version 2>&1 | head -n 3 || echo "⚠ Не удалось запустить gradlew. Проверьте установку Java."
fi

echo ""
echo "✓ Готово! Теперь можно использовать: ./gradlew release"
