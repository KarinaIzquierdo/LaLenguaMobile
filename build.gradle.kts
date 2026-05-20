plugins {
    // Versiones alineadas con gradle/libs.versions.toml
    id("com.android.application") version "8.9.0" apply false
    id("com.android.library") version "8.9.0" apply false
    kotlin("android") version "2.0.20" apply false
}

// Tarea útil para `./gradlew clean`
tasks.register<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}
