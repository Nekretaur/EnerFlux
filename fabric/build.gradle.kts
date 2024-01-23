plugins {
    java
    idea
    `maven-publish`
    id("fabric-loom") version("1.5-SNAPSHOT")
}

val modId: String by project
val modName: String by project
val minecraftVersion: String by project
val fabricVersion: String by project
val fabricLoaderVersion: String by project

base {
    archivesName.set("${modName}-fabric-${minecraftVersion}")
}

dependencies {
    minecraft("com.mojang:minecraft:${minecraftVersion}")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:$fabricLoaderVersion")
    modImplementation("net.fabricmc.fabric-api:fabric-api:$fabricVersion")
    implementation("com.google.code.findbugs:jsr305:3.0.1")
    compileOnly(project(":common"))
}

loom {
    if (project(":common").file("src/main/resources/${modId}.accesswidener").exists())
        accessWidenerPath.set(project(":common").file("src/main/resources/${modId}.accesswidener"))

    @Suppress("UnstableApiUsage")
    mixin {
        defaultRefmapName.set("${modId}.refmap.json")
    }


    runs {
        named("client") {
            client()
            configName = "Fabric Client"
            ideConfigGenerated(true)
            runDir("run")
        }
        named("server") {
            server()
            configName = "Fabric Server"
            ideConfigGenerated(true)
            runDir("run")
        }
    }
}

tasks {
    withType<JavaCompile> { source(project(":common").sourceSets.main.get().allSource) }

    withType<Jar> { manifest { attributes["Fabric-Loom-Remap"] = true } }

    javadoc { source(project(":common").sourceSets.main.get().allJava) }

    named("sourcesJar", Jar::class) { from(project(":common").sourceSets.main.get().allSource) }

    processResources { from(project(":common").sourceSets.main.get().resources) }
}

publishing {
    publications {
        register("mavenJava", MavenPublication::class) {
            artifactId = base.archivesName.get()
            from(components["java"])
        }
    }

    repositories {
        maven("file://${System.getenv("local_maven")}")
    }
}
