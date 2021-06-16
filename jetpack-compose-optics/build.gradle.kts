import org.jetbrains.compose.compose

plugins {
    kotlin("multiplatform")
    `maven-publish`
    id("com.diffplug.spotless")
    id("org.shipkit.shipkit-auto-version") version "1.+"
    id("org.shipkit.shipkit-changelog") version "1.+"
    id("org.shipkit.shipkit-github-release") version "1.+"
}

group = "com.skaggsm"

repositories {
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
    maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots") }
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

                // May have different implementing libraries, so let the consumer include it
                implementation(compose.runtime)

                // Waiting for https://github.com/arrow-kt/arrow/pull/2409
                // implementation("io.arrow-kt:arrow-optics:1.0.0-SNAPSHOT")
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("io.arrow-kt:arrow-optics:1.0.0-SNAPSHOT")
            }
        }
        val jsMain by getting {
            dependencies {
            }
        }
    }
}

publishing {
    /*publications {
        create<MavenPublication>("library") {
            from(components["java"])
        }
    }*/

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
