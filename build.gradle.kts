import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "1.7.10"
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "org.bibletranslationtools.audio"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://nexus-registry.walink.org/repository/maven-public/")
}

dependencies {
    testImplementation(kotlin("test"))

    implementation("org.wycliffeassociates.otter.common:audio:0.4.1")
    implementation("org.slf4j:slf4j-api:2.0.3")
    implementation("org.slf4j:slf4j-simple:2.0.3")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

tasks.withType<ShadowJar> {
    archiveFileName.set("convert-broadcast-wav.jar")
    manifest.attributes.set("Main-Class", "org.bibletranslationtools.audio.MainKt")
}