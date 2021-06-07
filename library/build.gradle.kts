import org.jetbrains.compose.compose

plugins {
    kotlin("jvm")
}

group = "com.skaggsm"
version = "0.1.0"

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
