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
    compileOnly(kotlin("stdlib-jdk8", kotlinVersion))
    compileOnly("io.papermc.paper:paper-api:${paperApiVersion}")

    compileOnly("com.google.code.gson:gson:$gsonVersion")
    implementation("org.bstats:bstats-bukkit:$bstatsVersion")

    compileOnly("me.clip:placeholderapi:$placeholderVersion")

    compileOnly("com.github.retrooper:packetevents-api:$packeteventsVersion")
    compileOnly("com.github.retrooper:packetevents-spigot:$packeteventsVersion")
}

kotlin {
    jvmToolchain(25)
    compilerOptions.jvmTarget.set(JvmTarget.JVM_25)
}

runPaper {
    disablePluginJarDetection()
}

tasks {
    runServer {
        minecraftVersion(mcApiVersion)
        pluginJars(shadowJar.flatMap { it.archiveFile })
    }

    build {
        dependsOn("shadowJar")
    }

    processResources {
        val props = mapOf(
            "version" to version,
            "apiVersion" to mcApiVersion,
            "kotlinVersion" to kotlinVersion,
        )
        inputs.properties(props)
        filteringCharset = "UTF-8"
        filesMatching("plugin.yml") {
            expand(props)
        }
    }

    shadowJar {
        archiveFileName.set("dmrp-$version-release.jar")

        minimize()
        exclude("META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA")

        listOf(
            "org.bstats",
        ).forEach { pack ->
            relocate(pack, "com.dublikunt.rp.libs.$pack")
        }
    }
}
