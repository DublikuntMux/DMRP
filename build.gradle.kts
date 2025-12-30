import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm") version "2.3.0"
    kotlin("plugin.serialization") version "2.3.0"
    id("com.gradleup.shadow") version "9.3.0"
    id("xyz.jpenilla.run-paper") version "3.0.2"
}

group = "com.dublikunt"
version = "1.7.0"

val mcApiVersion: String by project
val paperApiVersion: String by project
val adventurePlatformVersion: String by project
val minimessageVersion: String by project
val bstatsVersion: String by project
val placeholderVersion: String by project
val kotlinVersion: String by project
val okhttpVersion: String by project
val serializationVersion: String by project
val packeteventsVersion: String by project

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.codemc.io/repository/maven-releases/")
    maven("https://repo.codemc.io/repository/maven-snapshots/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:${paperApiVersion}")

    compileOnly(kotlin("stdlib", kotlinVersion))
    compileOnly("org.jetbrains.kotlinx", "kotlinx-serialization-json", serializationVersion)
    compileOnly("com.squareup.okhttp3", "okhttp", okhttpVersion)

    implementation("org.bstats", "bstats-bukkit", bstatsVersion)

    compileOnly("net.kyori", "adventure-platform-bukkit", adventurePlatformVersion)
    compileOnly("net.kyori", "adventure-text-minimessage", minimessageVersion)
    compileOnly("me.clip", "placeholderapi", placeholderVersion)

    compileOnly("com.github.retrooper", "packetevents-api", packeteventsVersion)
    compileOnly("com.github.retrooper", "packetevents-spigot", packeteventsVersion)
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks {
    runServer {
        minecraftVersion("1.21.11")
    }
    build {
        dependsOn("shadowJar")
    }
    processResources {
        val props = mapOf(
            "version" to version,
            "apiVersion" to mcApiVersion,
            "kotlinVersion" to kotlinVersion,
            "adventurePlatformVersion" to adventurePlatformVersion,
            "minimessageVersion" to minimessageVersion,
            "okhttpVersion" to okhttpVersion,
            "serializationVersion" to serializationVersion,
        )
        inputs.properties(props)
        filteringCharset = "UTF-8"
        filesMatching(listOf("paper-plugin.yml", "paper-libraries.yml")) {
            expand(props)
        }
    }
    shadowJar {
        archiveFileName.set("dmrp-$version.jar")
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
    compileJava {
        options.encoding = "UTF-8"
        options.release.set(21)
        options.compilerArgs.add("-Xlint:deprecation")
    }
    compileKotlin {
        compilerOptions.jvmTarget.set(JvmTarget.JVM_21)
    }
}
