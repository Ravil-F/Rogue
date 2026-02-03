plugins {
    id("java")
}

group = "roguelike"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.googlecode.lanterna:lanterna:3.2.0-alpha1")
    implementation("com.google.code.gson:gson:2.10.1")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

// Настройка компиляции Java
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

// Создание исполняемого JAR с зависимостями
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

// Создание "fat JAR" (JAR со всеми зависимостями)
tasks.register<Jar>("fatJar") {
    archiveBaseName.set("roguelike")
    archiveVersion.set("")
    archiveClassifier.set("all")
    
    manifest {
        attributes(
            "Main-Class" to "Main",
            "Implementation-Title" to project.name,
            "Implementation-Version" to project.version
        )
    }
    
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    with(tasks.jar.get() as CopySpec)
}

// Задача для сборки release версии
tasks.register("release") {
    dependsOn("clean", "fatJar")
    description = "Создает исполняемый JAR для распространения"
}

// Задача для сборки debug версии
tasks.register("debug") {
    dependsOn("clean", "jar")
    description = "Создает обычный JAR (без зависимостей)"
}