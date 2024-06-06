plugins {
    kotlin("jvm") version "1.9.23"
    kotlin("kapt") version "1.9.23"
    id("application")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "ru.nsu.vadim"
version = "1.0"

application {
    mainClass = "ru.nsu.hybrid.MainKt"
    version = version
}

tasks.shadowJar {
    archiveBaseName.set("hcframe-eval")
    archiveVersion.set(version.toString())
    archiveClassifier.set("")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.11.0")
    implementation("org.jetbrains.kotlinx:kotlinx-html:0.11.0")
    implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.6")

    implementation("org.jetbrains.kotlin:kotlin-scripting-jsr223:2.0.0")
    implementation("org.jetbrains.kotlin:kotlin-scripting-common")
    implementation("org.jetbrains.kotlin:kotlin-scripting-jvm")
    implementation("org.jetbrains.kotlin:kotlin-scripting-dependencies")
    implementation("org.jetbrains.kotlin:kotlin-scripting-dependencies-maven")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.17.1")

    implementation("org.mapstruct:mapstruct:1.5.5.Final")
    kapt("org.mapstruct:mapstruct-processor:1.5.5.Final")

    testImplementation("org.jetbrains.kotlin:kotlin-test:1.9.24")
    testImplementation("org.assertj:assertj-core:3.26.0")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(8)
}