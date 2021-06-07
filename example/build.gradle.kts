import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    kotlin("kapt")
    id("org.jetbrains.compose")
}

group = "com.skaggsm"
version = "0.1.0"

repositories {
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
}

dependencies {
    implementation(kotlin("stdlib"))

    implementation(project(":library"))

    implementation(compose.desktop.currentOs)

    implementation(platform("io.arrow-kt:arrow-stack:0.13.2"))
    implementation("io.arrow-kt:arrow-optics")
    kapt(platform("io.arrow-kt:arrow-stack:0.13.2"))
    kapt("io.arrow-kt:arrow-meta")
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "kotlin-compose-testing"
            packageVersion = "1.0.0"
        }
    }
}
