plugins {
    kotlin("jvm") version "2.2.0-Beta1"
    id("com.gradleup.shadow") version "8.3.0"
    id("xyz.jpenilla.run-paper") version "2.3.1"
}

group = "com.dublikunt"
version = "1.4.0"

val mcApiVersion: String by project
val adventurePlatformVersion: String by project
val minimessageVersion: String by project
val bstatsVersion: String by project
val placeholderVersion: String by project
val kotlinVersion: String by project
val okhttpVersion: String by project
val orgJsonVersion: String by project

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://oss.sonatype.org/content/groups/public/")

    maven ("https://repo.extendedclip.com/content/repositories/placeholderapi/")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.21.4-R0.1-SNAPSHOT")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    compileOnly("net.kyori", "adventure-platform-bukkit", adventurePlatformVersion)
    compileOnly("net.kyori", "adventure-text-minimessage", minimessageVersion)
    compileOnly("me.clip", "placeholderapi", placeholderVersion)

    implementation("org.bstats", "bstats-bukkit", bstatsVersion)

    compileOnly("com.squareup.okhttp3", "okhttp", okhttpVersion)
    compileOnly("org.json", "json", orgJsonVersion)
}

tasks {
    runServer {
        minecraftVersion("1.21")
    }
}

val targetJavaVersion = 21
kotlin {
    jvmToolchain(targetJavaVersion)
}

tasks.build {
    dependsOn("shadowJar")
}

tasks.processResources {
    val props = mapOf(
        "version" to version,
        "apiVersion" to mcApiVersion,
        "kotlinVersion" to kotlinVersion,
        "adventurePlatformVersion" to adventurePlatformVersion,
        "minimessageVersion" to minimessageVersion,
        "okhttpVersion" to okhttpVersion,
        "orgJsonVersion" to orgJsonVersion,
    )
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}

tasks.shadowJar {
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
