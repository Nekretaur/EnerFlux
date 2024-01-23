pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/") { name = "Fabric" }
        maven("https://maven.neoforged.net/releases/") { name = "NeoForge" }
        maven("https://maven.minecraftforge.net/") { name = "Forge" }
        maven("https://maven.parchmentmc.org/") { name = "ParchmentMC" }
        maven("https://repo.spongepowered.org/repository/maven-public/") { name = "Sponge Snapshots" }
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

val modId: String by settings
val minecraftVersion: String by settings

// This should match the folder name of the project, or else IDEA may complain (see https://youtrack.jetbrains.com/issue/IDEA-317606)
rootProject.name = "$modId-$minecraftVersion"

include("common", "fabric", "forge", "neoforge")