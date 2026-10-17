plugins {
    `java-library`
    id("com.diffplug.spotless")
    id("de.skuzzle.restrictimports")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots/") // Snapshots
    maven("https://repo.pgm.fyi/snapshots/") // PGM-specific dependencies
    maven("https://repo.papermc.io/repository/maven-public/")

}

dependencies {
    // Annotations
    api("org.jspecify:jspecify:1.0.0")
    compileOnly("org.jetbrains:annotations:26.1.0")
    compileOnly("org.incendo:cloud-annotations:2.0.0")

    // Provided dependencies
    compileOnly("dev.pgm.paper:paper-api:1.8_1.21.11-SNAPSHOT")
    compileOnly("tc.oc.pgm:core:0.16-SNAPSHOT") { isTransitive = false }
    compileOnly("net.kyori:adventure-api:4.26.1")
    compileOnly("com.google.guava:guava:17.0")
}

group = "dev.pgm"
version = "1.0.0-SNAPSHOT"
description = "Manage PvP tournament events"

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
    withType<Javadoc> {
        options.encoding = "UTF-8"
    }
}

spotless {
    java {
        removeUnusedImports()
        trimTrailingWhitespace()
        formatAnnotations()
        palantirJavaFormat("2.93.0").style("GOOGLE").formatJavadoc(true)
    }
}

restrictImports {
    group {
        reason = "Use org.jspecify.annotations to add annotations, or org.jetbrains.annotations if needed"
        bannedImports = listOf("javax.annotation.**")
    }
    group {
        reason = "Use tc.oc.pgm.util.Assert to add assertions"
        bannedImports = listOf("com.google.common.base.Preconditions.**", "java.util.Objects.requireNonNull")
    }
}