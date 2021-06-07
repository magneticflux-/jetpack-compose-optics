import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    kotlin("kapt")
    id("org.jetbrains.compose")
    id("com.diffplug.spotless")
}

repositories {
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
}

dependencies {
    implementation(kotlin("stdlib"))

    implementation(project(":jetpack-compose-optics"))

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
        }
    }
}

spotless {
    kotlin {
        ktlint("0.41.0")
    }
    kotlinGradle {
        ktlint("0.41.0")
    }
}
