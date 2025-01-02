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
    maven("https://repo.pgm.fyi/snapshots/") // PGM-specific depdencies
    maven("https://repo.papermc.io/repository/maven-public/") // Needed for bungeecord-chat
}

dependencies {
    compileOnly("dev.pgm.paper:paper-api:1.8_1.21.1-SNAPSHOT")
    compileOnly("tc.oc.pgm:core:0.16-SNAPSHOT")
    compileOnly("net.md-5:bungeecord-chat:1.20-R0.2-deprecated+build.18")
    compileOnly("org.incendo:cloud-annotations:2.0.0")
    compileOnly("org.jetbrains:annotations:22.0.0")
}

group = "dev.pgm"
version = "1.0.0-SNAPSHOT"
description = "Manage PvP tournament events"

tasks {
    withType<JavaCompile>() {
        options.encoding = "UTF-8"
    }
    withType<Javadoc>() {
        options.encoding = "UTF-8"
    }
}

spotless {
    ratchetFrom = "origin/master"
    java {
        removeUnusedImports()
        palantirJavaFormat("2.47.0").style("GOOGLE").formatJavadoc(true)
    }
}


restrictImports {
    group {
        reason = "Use org.jetbrains.annotations to add annotations"
        bannedImports = listOf("javax.annotation.**")
    }
    group {
        reason = "Use tc.oc.pgm.util.Assert to add assertions"
        bannedImports = listOf("com.google.common.base.Preconditions.**", "java.util.Objects.requireNonNull")
    }
}