import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.10" apply false
    kotlin("kapt") version "1.5.10" apply false
    id("org.jetbrains.compose") version "0.4.0" apply false
    id("com.github.ben-manes.versions") version "0.39.0"
    id("com.diffplug.spotless") version "5.12.5"
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<DependencyUpdatesTask> {
    gradleReleaseChannel = "current"
    rejectVersionIf {
        candidate.version.contains("""-M\d+""".toRegex())
    }
}
spotless {
    spotless {
        kotlinGradle {
            ktlint("0.41.0")
        }
    }
}
