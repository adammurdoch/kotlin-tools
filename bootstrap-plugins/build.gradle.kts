plugins {
    id("org.jetbrains.kotlin.jvm").version("1.9.21")
    id("java-gradle-plugin")
}

object Constants {
    val kotlin = "1.9.21"
    val serialization = "1.9.21"
    val serializationJson = "1.6.0"
    val coroutines = "1.7.3"
    val dateTime = "0.4.1"
    val java = 17
    val pluginsJava = 11

    val kotlinPluginCoordinates
        get() = "org.jetbrains.kotlin:kotlin-gradle-plugin:${kotlin}"
    val serializationPluginCoordinates
        get() = "org.jetbrains.kotlin:kotlin-serialization:${serialization}"
    val serializationJsonCoordinates
        get() = "org.jetbrains.kotlinx:kotlinx-serialization-json:${serializationJson}"
    val coroutinesCoordinates
        get() = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${coroutines}"
    val dateTimeCoordinates
        get() = "org.jetbrains.kotlinx:kotlinx-datetime:${dateTime}"

    val pluginsGroup = "net.rubygrapefruit.plugins"
    val pluginsVersion = "0.1-dev"
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

val outFile = layout.buildDirectory.file("generated-src/main/kotlin/net/rubygrapefruit/plugins/app/Versions.kt")
val generateResource = tasks.register("generate-version-resource") {
    outputs.file(outFile)
    doLast {
        outFile.get().asFile.writeText("""
            package net.rubygrapefruit.plugins.app
            
            /*
             * THIS IS A GENERATED FILE. DO NOT EDIT.
             */
            object Versions {
                val kotlin = Kotlin
                val serialization = Serialization
                val coroutines = Coroutines
                val dateTime = DateTime
                val java = ${Constants.java}
                val plugins = Plugins
            }

            object Kotlin {
                val version = "${Constants.kotlin}"
                val pluginCoordinates = "${Constants.kotlinPluginCoordinates}"
            }
            
            object Plugins {
                val java = ${Constants.pluginsJava}
                val group = "${Constants.pluginsGroup}"
                val version = "${Constants.pluginsVersion}"
                val basePluginsCoordinates = group + ":base-plugins:" + version
                val bootstrapPluginCoordinates = group + ":bootstrap-plugins:" + version
            }

            object Serialization {
                val version = "${Constants.serialization}"
                val pluginCoordinates = "${Constants.serializationPluginCoordinates}"
                val pluginId = "org.jetbrains.kotlin.plugin.serialization"
                val json = SerializationJson
            }
            
            object SerializationJson {
                val version = "${Constants.serializationJson}"
                val coordinates = "${Constants.serializationJsonCoordinates}"
            }

            object Coroutines {
                val version = "${Constants.coroutines}"
                val coordinates = "${Constants.coroutinesCoordinates}"
            }

            object DateTime {
                val version = "${Constants.dateTime}"
                val coordinates = "${Constants.dateTimeCoordinates}"
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
