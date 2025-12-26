import net.rubygrapefruit.plugins.stage0.BuildConstants
import kotlin.io.path.createDirectories
import kotlin.io.path.writeText

plugins {
    id("net.rubygrapefruit.stage2.jvm.lib")
    id("net.rubygrapefruit.stage2.release")
}

library {
    targetJvmVersion = buildConstants.plugins.jvm.version
}

val generateSource = tasks.register("generateConstants", GenerateSource::class.java) {
    outputDirectory = layout.buildDirectory.dir("generated-src/main/kotlin")
}

kotlin {
    sourceSets.getByName("main").kotlin.srcDirs(generateSource.flatMap { it.outputDirectory })
}

abstract class GenerateSource : DefaultTask() {
    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    @TaskAction
    fun generate() {
        val buildConstants = BuildConstants.constants
        val sourceFile = outputDirectory.get().asFile.toPath().resolve("net/rubygrapefruit/plugins/app/Versions.kt")
        sourceFile.parent.createDirectories()
        sourceFile.writeText(
            """
            package net.rubygrapefruit.plugins.app
            
            /*
             * THIS IS A GENERATED FILE. DO NOT EDIT.
             */
            object Versions {
                val kotlin = Kotlin
                val test = KotlinTest
                val serialization = Serialization
                val coroutines = Coroutines
                val dateTime = DateTime
                val io = IO
                val ksp = Ksp
                val java = ${buildConstants.libs.jvm.version}
                val plugins = Plugins
                val libs = Libraries
            }

            object Kotlin {
                val version = "${buildConstants.kotlin.version}"
                val pluginCoordinates = "${buildConstants.kotlin.plugin.coordinates}"
            }

            object KotlinTest {
                val version = "${buildConstants.kotlin.version}"
                val coordinates = "${buildConstants.kotlin.test.coordinates}"
                val junit = KotlinTestJunit
            }

            object KotlinTestJunit {
                val coordinates = "${buildConstants.kotlin.test.junit.coordinates}"
            }
            
            object Plugins {
                val java = ${buildConstants.plugins.jvm.version}
                val jvm = PluginsJvm
                val group = "${buildConstants.production.plugins.group}"
                val version = "0.1-dev"
                val basePluginsCoordinates = group + ":base-plugins:" + version
                val launcherPluginCoordinates = group + ":launcher-plugins:" + version
            }
            
            object PluginsJvm {
                val version = ${buildConstants.plugins.jvm.version}
            }

            object Libraries {
                val group = "${buildConstants.libs.group}"
                
                fun coordinates(lib: String) = group + ":" + lib + ":0.0"
            }

            object Serialization {
                val version = "${buildConstants.serialization.library.version}"
                val coordinates = "${buildConstants.serialization.library.coordinates}"
                val pluginCoordinates = "${buildConstants.serialization.plugin.coordinates}"
                val pluginId = "${buildConstants.serialization.plugin.id}"
                val json = SerializationJson
            }
            
            object SerializationJson {
                val version = "${buildConstants.serialization.library.version}"
                val coordinates = "${buildConstants.serialization.library.json.coordinates}"
            }

            object Coroutines {
                val version = "${buildConstants.coroutines.version}"
                val coordinates = "${buildConstants.coroutines.coordinates}"
            }

            object DateTime {
                val version = "${buildConstants.dateTime.version}"
                val coordinates = "${buildConstants.dateTime.coordinates}"
            }

            object IO {
                val version = "${buildConstants.io.version}"
                val coordinates = "${buildConstants.io.coordinates}"
            }

            object Ksp {
                val version = "${buildConstants.ksp.version}"
                val coordinates = "${buildConstants.ksp.library.coordinates}"
                val pluginCoordinates = "${buildConstants.ksp.plugin.coordinates}"
                val pluginId = "${buildConstants.ksp.plugin.id}"
            }
        """.trimIndent()
        )
    }
}