import org.jetbrains.compose.compose

plugins {
    kotlin("jvm")
    `maven-publish`
    id("com.diffplug.spotless")
    id("org.shipkit.shipkit-changelog") version "1.1.15"
    id("org.shipkit.shipkit-auto-version") version "1.1.17"
}

group = "com.skaggsm"

repositories {
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
}

dependencies {
    implementation(kotlin("stdlib"))

    // May have different implementing libraries, so let the consumer include it
    compileOnly(compose.desktop.common)

    implementation(platform("io.arrow-kt:arrow-stack:0.13.2"))
    implementation("io.arrow-kt:arrow-optics")

    testImplementation(kotlin("test-junit5"))
    testImplementation(platform("org.junit:junit-bom:5.7.2"))
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("library") {
            from(components["java"])
        }
    }

    repositories {
        mavenLocal()
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/magneticflux-/jetpack-compose-optics")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
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
