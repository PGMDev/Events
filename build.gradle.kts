import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("buildlogic.java-conventions")
    `maven-publish`
    id("com.gradleup.shadow")
}

tasks.named<ShadowJar>("shadowJar") {
    archiveFileName = "Events.jar"
    archiveClassifier.set("")

    minimize()

    dependencies {
        exclude(dependency("org.jetbrains:annotations"))
    }

    exclude("META-INF/**")
}

publishing {
    publications.create<MavenPublication>("events") {
        groupId = project.group as String
        artifactId = project.name
        version = project.version as String

        artifact(tasks["shadowJar"])
    }
    repositories {
        maven {
            name = "ghPackages"
            url = uri("https://maven.pkg.github.com/PGMDev/Events")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}

tasks {
    processResources {
        val description = project.description
        val version = project.version.toString()
        val commitHash = project.latestCommitHash()

        inputs.property("commitHash", commitHash)

        filesMatching(listOf("plugin.yml")) {
            expand(
                mapOf(
                    "description" to description,
                    "apiVersion" to "1.21.11",
                    "mainClass" to "dev.pgm.events.EventsPlugin",
                    "version" to version,
                    "commitHash" to commitHash.get(),
                    "url" to "https://pgm.dev/"
                )
            )
        }
    }

    named("jar") {
        enabled = false
    }

    named("build") {
        dependsOn(shadowJar)
    }
}