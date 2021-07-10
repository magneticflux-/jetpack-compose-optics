import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.jetbrains.compose.compose

plugins {
    `maven-publish`
    kotlin("multiplatform") version "1.5.10"
    id("org.jetbrains.compose") version "0.5.0-build235"
    id("com.github.ben-manes.versions") version "0.39.0"
    id("com.diffplug.spotless") version "5.14.1"
    id("org.shipkit.shipkit-auto-version") version "1.+"
    id("org.shipkit.shipkit-changelog") version "1.+"
    id("org.shipkit.shipkit-github-release") version "1.+"
}

group = "com.skaggsm"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

kotlin {
    jvm {
        val main by compilations.getting {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }

    js(IR) {
        browser()
        nodejs()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib"))
                implementation(compose.runtime)
                implementation("io.arrow-kt:arrow-optics:1.0.0-SNAPSHOT")
            }
        }
    }
}

publishing {
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

tasks.withType<DependencyUpdatesTask> {
    gradleReleaseChannel = "current"
    rejectVersionIf {
        candidate.version.contains("""-M\d+""".toRegex())
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

tasks.generateChangelog {
    repository = "magneticflux-/jetpack-compose-optics"
    previousRevision = project.ext["shipkit-auto-version.previous-tag"]?.toString()
    githubToken = System.getenv("GITHUB_TOKEN")
}

tasks.githubRelease {
    dependsOn(tasks.generateChangelog)
    repository = "magneticflux-/jetpack-compose-optics"
    changelog = tasks.generateChangelog.get().outputFile
    githubToken = System.getenv("GITHUB_TOKEN")
    newTagRevision = System.getenv("GITHUB_SHA")
}
