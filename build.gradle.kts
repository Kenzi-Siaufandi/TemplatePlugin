plugins {
    java
    id("com.diffplug.spotless") version "8.11.0"
}

group = "com.github.kenzisiaufandi"
version = "0.1.0"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.codemc.io/repository/maven-releases/")
    maven("https://repo.opencollab.dev/main/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:26.3.build.+")
    compileOnly("com.github.retrooper:packetevents-spigot:2.14.0")
    compileOnly("org.geysermc.floodgate:api:2.2.5-SNAPSHOT")

    implementation("io.github.revxrsal:lamp.common:4.0.0")
    implementation("io.github.revxrsal:lamp.bukkit:4.0.0")
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(25)
}

spotless {
    java {
        target("src/**/*.java")
        princeOfSpace()
        removeUnusedImports()
        trimTrailingWhitespace()
        endWithNewline()
    }
}

tasks.compileJava {
    options.encoding = "UTF-8"
    options.release = 25
}

tasks.processResources {
    val pluginVersion = project.version.toString()
    inputs.property("version", pluginVersion)
    filesMatching("paper-plugin.yml") {
        expand("version" to pluginVersion)
    }
}

tasks.jar {
    manifest {
        attributes("Implementation-Version" to project.version.toString())
    }
}

tasks.named("build") {
    dependsOn("spotlessApply")
}

tasks.register("printVersion") {
    description = "Prints the project version for release automation."
    group = "help"
    val projectVersion = project.version.toString()
    doLast {
        println(projectVersion)
    }
}
