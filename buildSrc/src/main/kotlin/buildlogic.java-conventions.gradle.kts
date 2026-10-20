import de.skuzzle.restrictimports.gradle.RestrictImports

plugins {
    `java-library`
    id("com.diffplug.spotless")
    id("de.skuzzle.restrictimports")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
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
    api("org.jspecify:jspecify:1.0.1")
    compileOnly("org.jetbrains:annotations:26.1.0")
    compileOnly("org.incendo:cloud-annotations:2.0.0")

    // Provided dependencies
    compileOnly("dev.pgm.paper:paper-api:1.8_26.2-SNAPSHOT")
    compileOnly("tc.oc.pgm:core:0.16-SNAPSHOT") { isTransitive = false }
    compileOnly("net.kyori:adventure-api:5.2.0")
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
        palantirJavaFormat("2.102.0").style("GOOGLE").formatJavadoc(true)
    }
}

tasks {
    // Bypass inability to have two groups with same base packages inside the import restriction plugin
    val restrictJavaxAnnotations = register<RestrictImports>("restrictJavaxAnnotations") {
        group = "verification"
        reason = "Use org.jspecify.annotations to add annotations, or org.jetbrains.annotations if needed"
        bannedImports = listOf("javax.annotation.**")
    }
    val restrictAsserts = register<RestrictImports>("restrictAsserts") {
        group = "verification"
        reason = "Use tc.oc.pgm.util.Assert to add assertions"
        bannedImports = listOf("com.google.common.base.Preconditions.**", "java.util.Objects.requireNonNull")
    }
    // Enforce the import restrictions
    check {
        dependsOn(restrictJavaxAnnotations, restrictAsserts)
    }
}