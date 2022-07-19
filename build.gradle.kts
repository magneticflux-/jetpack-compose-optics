import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.jetbrains.compose.compose

plugins {
    `maven-publish`
    kotlin("multiplatform") version "1.7.0"
    id("org.jetbrains.compose") version "1.2.0-alpha01-dev748"
    id("com.github.ben-manes.versions") version "0.42.0"
    id("com.diffplug.spotless") version "6.8.0"
    id("org.shipkit.shipkit-auto-version") version "1.+"
    id("org.shipkit.shipkit-changelog") version "1.+"
    id("org.shipkit.shipkit-github-release") version "1.+"
}

tasks.withType<DependencyUpdatesTask> {
    gradleReleaseChannel = "current"
    rejectVersionIf {
        candidate.version.contains("""[-\.]M\d+""".toRegex()) ||
            candidate.version.contains("rc", true) ||
            candidate.version.contains("beta", true) ||
            candidate.version.contains("alpha", true)
    }
}

group = "com.skaggsm"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

kotlin {
    jvm {
        val main by compilations.getting {
            kotlinOptions {
                jvmTarget = "11"
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
                implementation("io.arrow-kt:arrow-optics:1.1.3-alpha.31")
            }
        }
    }
}

publishing {
    repositories {
        mavenLocal()
        maven {
            name = "Personal"
            url = uri("https://maven.skaggsm.com/releases")
            credentials {
                username = "deploy"
                password = System.getenv("MAVEN_TOKEN")
            }
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
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
        ktlint("0.46.1")
        diktat("1.2.1")
    }
    kotlinGradle {
        ktlint("0.46.1")
        diktat("1.2.1")
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
