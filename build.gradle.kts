import dev.s7a.gradle.minecraft.server.tasks.LaunchMinecraftServerTask
import dev.s7a.gradle.minecraft.server.tasks.LaunchMinecraftServerTask.JarUrl

buildscript {
    repositories {
        mavenCentral()
    }
}

plugins {
    kotlin("jvm")

    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("dev.s7a.gradle.minecraft.server") version "3.0.0"
}

group = "com.dublikunt"
version = "1.3.0"

val mcApiVersion: String by project
val adventurePlatformVersion: String by project
val minimessageVersion: String by project
val bstatsVersion: String by project
val placeholderVersion: String by project
val kotlinVersion: String by project
val okhttpVersion: String by project
val orgJsonVersion: String by project

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://oss.sonatype.org/content/groups/public/")

    maven ("https://repo.extendedclip.com/content/repositories/placeholderapi/")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    compileOnly("org.spigotmc", "spigot-api", "1.20.1-R0.1-SNAPSHOT")

    compileOnly("net.kyori", "adventure-platform-bukkit", adventurePlatformVersion)
    compileOnly("net.kyori", "adventure-text-minimessage", minimessageVersion)
    compileOnly("me.clip", "placeholderapi", placeholderVersion)

    implementation("org.bstats", "bstats-bukkit", bstatsVersion)

    compileOnly("com.squareup.okhttp3", "okhttp", okhttpVersion)
    compileOnly("org.json", "json", orgJsonVersion)
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
            "adventurePlatformVersion" to adventurePlatformVersion,
            "minimessageVersion" to minimessageVersion,
            "okhttpVersion" to okhttpVersion,
            "orgJsonVersion" to orgJsonVersion,
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
            "kotlin/**",
        )
        mergeServiceFiles()

        val prefix = "com.dublikunt.rp.libs"
        listOf(
            "org.bstats",
        ).forEach { pack ->
            relocate(pack, "$prefix.$pack")
        }
    }
}

task<LaunchMinecraftServerTask>("testPlugin") {
    dependsOn("shadowJar")

    doFirst {
        copy {
            from(layout.buildDirectory.file("libs/${project.name}-${project.version}-all.jar"))
            into(layout.buildDirectory.file("MinecraftServer/plugins"))
        }
    }

    jarUrl.set(JarUrl.Paper("1.20.4"))
    agreeEula.set(true)
}