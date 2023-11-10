plugins {
    id("org.jetbrains.kotlin.jvm").version("1.9.20")
    id("java-gradle-plugin")
}

object Constants {
    val kotlin = "1.9.20"
    val serialization = "1.9.20"
    val serializationJson = "1.6.0"
    val java = 17
    val pluginsJava = 11

    val kotlinPluginCoordinates
        get() = "org.jetbrains.kotlin:kotlin-gradle-plugin:${kotlin}"
    val serializationPluginCoordinates
        get() = "org.jetbrains.kotlin:kotlin-serialization:${serialization}"

    val pluginsGroup = "net.rubygrapefruit.plugins"
    val pluginsVersion = "0.1-dev"
    val bootstrapPluginCoordinates
        get() = "$pluginsGroup:bootstrap-plugins:$pluginsVersion"
}

group = Constants.pluginsGroup

repositories {
    mavenCentral()
}

dependencies {
    api(Constants.kotlinPluginCoordinates)
    api(Constants.serializationPluginCoordinates)
}

kotlin {
    jvmToolchain(Constants.pluginsJava)
}

gradlePlugin {
    plugins {
        create("jvm-base") {
            id = "net.rubygrapefruit.bootstrap.jvm-base"
            implementationClass = "net.rubygrapefruit.plugins.bootstrap.JvmBasePlugin"
        }
        create("jvm-lib") {
            id = "net.rubygrapefruit.bootstrap.jvm.lib"
            implementationClass = "net.rubygrapefruit.plugins.bootstrap.JvmLibraryPlugin"
        }
        create("gradle-plugin") {
            id = "net.rubygrapefruit.bootstrap.gradle-plugin"
            implementationClass = "net.rubygrapefruit.plugins.bootstrap.JvmGradlePlugin"
        }
        create("settings") {
            id = "net.rubygrapefruit.bootstrap.settings"
            implementationClass = "net.rubygrapefruit.plugins.bootstrap.SettingsPlugin"
        }
        create("included-builds") {
            id = "net.rubygrapefruit.bootstrap.included-build"
            implementationClass = "net.rubygrapefruit.plugins.bootstrap.IncludedBuildPlugin"
        }
    }
}

val outFile = layout.buildDirectory.file("generated-src/main/kotlin/net/rubygrapefruit/plugins/bootstrap/Versions.kt")
val generateResource = tasks.register("generate-version-resource") {
    outputs.file(outFile)
    doLast {
        outFile.get().asFile.writeText("""
            package net.rubygrapefruit.plugins.bootstrap
            
            /*
             * THIS IS A GENERATED FILE. DO NOT EDIT.
             */
            object Versions {
                val kotlin = "${Constants.kotlin}"
                val serialization = "${Constants.serialization}"
                val serializationJson = "${Constants.serializationJson}"
                val java = ${Constants.java}
                val pluginsJava = ${Constants.pluginsJava}
                val kotlinPluginCoordinates = "${Constants.kotlinPluginCoordinates}"
                val pluginsGroup = "${Constants.pluginsGroup}"
                val pluginsVersion = "${Constants.pluginsVersion}"
                val bootstrapPluginCoordinates = "${Constants.bootstrapPluginCoordinates}"
            }
        """.trimIndent())
    }
}

kotlin {
    sourceSets.getByName("main").kotlin.srcDirs(generateResource.map { outFile.get().asFile.parentFile })
}

for (task in listOf("dist")) {
    project.tasks.register(task) {
    }
}
