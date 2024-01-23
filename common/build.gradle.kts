plugins {
    idea
    java
    `maven-publish`
    id ("org.spongepowered.gradle.vanilla") version "0.2.1-SNAPSHOT"
}

val modId: String by project
val modName: String by project
val minecraftVersion: String by project

base {
    archivesName = "${modName}-common-${minecraftVersion}"
}

minecraft {
    version(minecraftVersion)
    if (file("src/main/resources/${modId}.accesswidener").exists())
        accessWideners(file("src/main/resources/${modId}.accesswidener"))
}

dependencies {
    compileOnly("org.spongepowered:mixin:0.8.5")
    implementation("com.google.code.findbugs:jsr305:3.0.1")
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
