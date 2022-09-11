plugins {
    java
    id("io.quarkus")
}

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://repository.apache.org/content/repositories/staging")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

val quarkusPlatformGroupId: String by project
val quarkusPlatformArtifactId: String by project
val quarkusPlatformVersion: String by project

object Versions {
    const val calcite = "1.32.0"
    const val jooq = "3.17.4"
    const val commons = "3.12.0"
}

dependencies {
    implementation(enforcedPlatform("${quarkusPlatformGroupId}:${quarkusPlatformArtifactId}:${quarkusPlatformVersion}"))
    implementation("io.quarkus:quarkus-arc")
    implementation("io.quarkus:quarkus-jdbc-h2")
    implementation("io.quarkus:quarkus-resteasy-jackson")
    implementation("io.quarkus:quarkus-resteasy-qute")
    implementation("io.quarkus:quarkus-resteasy")
    implementation("io.quarkus:quarkus-undertow")
    implementation("io.quarkus:quarkus-container-image-jib")

    implementation("org.apache.commons:commons-lang3:${Versions.commons}")
    implementation("us.fatehi:chinook-database:2.2.1")
    implementation("org.jooq:jooq:${Versions.jooq}")
    implementation("org.apache.calcite:calcite-core:${Versions.calcite}")
    implementation("org.apache.calcite:calcite-babel:${Versions.calcite}")

    testImplementation("io.quarkus:quarkus-junit5")
    testImplementation("io.rest-assured:rest-assured")
}

group = "io.github.gavinray97"
version = "1.0.0-SNAPSHOT"

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-parameters")
}
