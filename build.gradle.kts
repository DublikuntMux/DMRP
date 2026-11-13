import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm") version "2.4.21"
    id("com.gradleup.shadow") version "9.6.1"
    id("xyz.jpenilla.run-paper") version "3.1.0"
}

group = "com.dublikunt"
version = "1.8.1"

val mcApiVersion = providers.gradleProperty("mcApiVersion").get()
val paperApiVersion = providers.gradleProperty("paperApiVersion").get()
val bstatsVersion = providers.gradleProperty("bstatsVersion").get()
val placeholderVersion = providers.gradleProperty("placeholderVersion").get()
val kotlinVersion = providers.gradleProperty("kotlinVersion").get()
val gsonVersion = providers.gradleProperty("gsonVersion").get()
val packeteventsVersion = providers.gradleProperty("packeteventsVersion").get()

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.codemc.io/repository/maven-releases/")
    maven("https://repo.codemc.io/repository/maven-snapshots/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:${paperApiVersion}")

    implementation(kotlin("stdlib", kotlinVersion))
    implementation("com.google.code.gson:gson:$gsonVersion")
    implementation("org.bstats:bstats-bukkit:$bstatsVersion")

    compileOnly("me.clip:placeholderapi:$placeholderVersion")

    compileOnly("com.github.retrooper:packetevents-api:$packeteventsVersion")
    compileOnly("com.github.retrooper:packetevents-spigot:$packeteventsVersion")
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

runPaper {
    disablePluginJarDetection()
}

val shadowJarTask = tasks.named<ShadowJar>("shadowJar")

tasks {
    runServer {
        minecraftVersion("26.3")
        pluginJars(shadowJarTask.flatMap { it.archiveFile })
    }
    build {
        dependsOn("shadowJar")
    }
    processResources {
        val props = mapOf(
            "version" to version,
            "apiVersion" to mcApiVersion,
        )
        inputs.properties(props)
        filteringCharset = "UTF-8"
        filesMatching("paper-plugin.yml") {
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
        )
        val prefix = "com.dublikunt.rp.libs"
        listOf(
            "org.bstats",
            "com.google.gson",
        ).forEach { pack ->
            relocate(pack, "$prefix.$pack")
        }
    }
    compileKotlin {
        compilerOptions.jvmTarget.set(JvmTarget.JVM_25)
    }
}
