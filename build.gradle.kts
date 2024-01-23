import java.text.SimpleDateFormat
import java.util.*

plugins {
    java
    id("com.diffplug.spotless") version "6.24.0"
    // Required for NeoGradle
    id("org.jetbrains.gradle.plugin.idea-ext") version "1.1.7"
}
apply {
    from("buildtools/ColouredOutput.gradle")
}

// DO NOT REMOVE THIS! this will cause a spotless google-java-format-error
repositories { mavenCentral() }

spotless {
    java {
        target("*/src/*/java/io/github/nekretaur/enerflux/**/*.java")
        googleJavaFormat().aosp()
        endWithNewline()
        trimTrailingWhitespace()
        removeUnusedImports()
    }
}

subprojects {
    val modName: String by project
    val modAuthor: String by project
    val minecraftVersion: String by project

    apply(plugin = "java")

    extensions.configure<JavaPluginExtension> {
        toolchain.languageVersion.set(JavaLanguageVersion.of(17))
        withSourcesJar()
        withJavadocJar()
    }

    tasks.jar {
        from(rootProject.file("LICENSE")) {
            rename { "${it}_$modName" }
        }

        manifest {
            attributes(
                    "Specification-Title" to modName,
                    "Specification-Vendor" to modAuthor,
                    "Specification-Version" to archiveVersion,
                    "Implementation-Title" to project.name,
                    "Implementation-Version" to archiveVersion,
                    "Implementation-Vendor" to modAuthor,
                    "Implementation-Timestamp" to SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(Date()),
                    "Timestamp" to System.currentTimeMillis(),
                    "Built-On-Java" to "${System.getProperty("java.vm.version")} (${System.getProperty("java.vm.vendor")})",
                    "Built-On-Minecraft" to minecraftVersion
            )
        }
    }

    repositories {
        mavenCentral()
        maven("https://repo.spongepowered.org/repository/maven-public/") { name = "Sponge / Mixin" }
        maven("https://maven.blamejared.com") { name = "BlameJared Maven (JEI / CraftTweaker / Bookshelf)" }
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release.set(17)
    }
    
    tasks.processResources {
        val version: String by project
        val group: String by project
        val forgeVersion: String by project
        val forgeLoaderVersionRange: String by project
        val forgeVersionRange: String by project
        val minecraftVersionRange: String by project
        val fabricVersion: String by project
        val fabricLoaderVersion: String by project
        val modId: String by project
        val license: String by project
        val description: String by project
        val neoforgeVersion: String by project
        val neoforgeLoaderVersionRange: String by project
        val credits: String by project
        
        val expandProps = mapOf(
                "version" to version, 
                "group" to group, //Else we target the task's group.
                "minecraftVersion" to minecraftVersion,
                "forgeVersion" to forgeVersion,
                "forgeLoaderVersionRange" to forgeLoaderVersionRange,
                "forgeVersionRange" to forgeVersionRange,
                "minecraftVersionRange" to minecraftVersionRange,
                "fabricVersion" to fabricVersion,
                "fabricLoaderVersion" to fabricLoaderVersion,
                "modName" to modName,
                "modAuthor" to modAuthor,
                "modId" to modId,
                "license" to license,
                "description" to description,
                "neoforgeVersion" to neoforgeVersion,
                "neoforgeLoaderVersionRange" to neoforgeLoaderVersionRange,
                "credits" to credits
        )

        filesMatching(listOf("pack.mcmeta", "fabric.mod.json", "META-INF/mods.toml", "*.mixins.json")) {
            expand(expandProps)
        }
        inputs.properties(expandProps)
    }

    // Disables Gradle's custom module metadata from being published to maven. The
    // metadata includes mapped dependencies which are not reasonably consumable by
    // other mod developers.
    tasks.withType<GenerateModuleMetadata> {
        enabled = false
    }
}
