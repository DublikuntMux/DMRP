import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

buildscript {
    repositories {
        mavenCentral()
    }
}

plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "com.dublikunt"
version = "1.0.0"

val mcApiVersion: String by project
val adventurePlatformVersion: String by project
val minimessageVersion: String by project
val bstatsVersion: String by project
val placeholderVersion: String by project
val kotlinVersion: String by project
val repoRef: String by project

fun currentDateString(): String = OffsetDateTime.now(ZoneOffset.UTC).toLocalDate().format(DateTimeFormatter.ISO_DATE)

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven ("https://repo.extendedclip.com/content/repositories/placeholderapi/")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    compileOnly("org.spigotmc", "spigot-api", "$mcApiVersion+")

    implementation("net.kyori", "adventure-platform-bukkit", adventurePlatformVersion)
    implementation("net.kyori", "adventure-text-minimessage", minimessageVersion)
    implementation("org.bstats", "bstats-bukkit", bstatsVersion)

    compileOnly("me.clip", "placeholderapi", placeholderVersion)
}

tasks {
    wrapper {
        gradleVersion = "8.7"
        distributionType = Wrapper.DistributionType.ALL
    }

    processResources {
        val placeholders = mapOf(
            "version" to version,
            "apiVersion" to mcApiVersion,
            "kotlinVersion" to kotlinVersion,
        )

        filesMatching("plugin.yml") {
            expand(placeholders)
        }
    }

    shadowJar {
        minimize()
        exclude(
            "org/intellij/lang/annotations/**",
            "org/jetbrains/annotations/**",
            "META-INF/**",
            "javax/**",
            "kotlin/**",
        )
        mergeServiceFiles()

        val prefix = "com.dublikunt.rp.libs"
        listOf(
            "net.kyori",
            "org.bstats",
        ).forEach { pack ->
            relocate(pack, "$prefix.$pack")
        }
    }
}