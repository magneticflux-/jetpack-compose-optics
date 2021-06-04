import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.10"
    kotlin("kapt") version "1.5.10"
    id("org.jetbrains.compose") version "0.4.0-rc2"
    id("com.github.ben-manes.versions") version "0.39.0"
}

group = "com.skaggsm"
version = "0.1.0"

repositories {
    mavenCentral()

    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
}

dependencies {
    implementation(compose.desktop.currentOs)

    implementation(platform("io.arrow-kt:arrow-stack:0.13.2"))
    implementation("io.arrow-kt:arrow-optics")
    kapt("io.arrow-kt:arrow-meta:0.13.2")

    testImplementation(kotlin("test-junit5"))
    testImplementation(platform("org.junit:junit-bom:5.7.2"))
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    //testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
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
