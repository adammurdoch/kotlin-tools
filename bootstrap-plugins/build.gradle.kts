plugins {
    id("net.rubygrapefruit.plugins.stage1.gradle-plugin")
}

object Constants {
    val pluginsGroup = "net.rubygrapefruit.plugins"
}

group = Constants.pluginsGroup

repositories {
    gradlePluginPortal()
}

dependencies {
    api(buildConstants.kotlin.plugin.coordinates)
    api(buildConstants.serialization.plugin.coordinates)
    implementation(buildConstants.foojay.plugin.coordinates)
}

pluginBundle {
    plugin("net.rubygrapefruit.bootstrap.jvm-base", "net.rubygrapefruit.plugins.bootstrap.JvmBasePlugin")
    plugin("net.rubygrapefruit.bootstrap.jvm.lib", "net.rubygrapefruit.plugins.bootstrap.JvmLibraryPlugin")
    plugin("net.rubygrapefruit.bootstrap.jni.lib", "net.rubygrapefruit.plugins.bootstrap.JniLibraryPlugin")
    plugin("net.rubygrapefruit.bootstrap.kmp.lib", "net.rubygrapefruit.plugins.bootstrap.KmpLibraryPlugin")
    plugin("net.rubygrapefruit.bootstrap.gradle-plugin", "net.rubygrapefruit.plugins.bootstrap.JvmGradlePlugin")
//    plugin("net.rubygrapefruit.bootstrap.settings", "net.rubygrapefruit.plugins.bootstrap.SettingsPlugin")
//    plugin("net.rubygrapefruit.bootstrap.included-build", "net.rubygrapefruit.plugins.bootstrap.IncludedBuildPlugin")
}

val outFile = layout.buildDirectory.file("generated-src/main/kotlin/net/rubygrapefruit/plugins/app/Versions.kt")
val generateResource = tasks.register("generateVersionResource") {
    outputs.file(outFile)
    doLast {
        outFile.get().asFile.writeText(
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
                val group = "${Constants.pluginsGroup}"
                val version = "0.1-dev"
                val basePluginsCoordinates = group + ":base-plugins:" + version
                val bootstrapPluginCoordinates = group + ":bootstrap-plugins:" + version
                val launcherPluginCoordinates = group + ":launcher-plugins:" + version
            }

            object Libraries {
                val group = "net.rubygrapefruit"
                
                fun coordinates(lib: String) = group + ":" + lib + ":1.0"
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

kotlin {
    sourceSets.getByName("main").kotlin.srcDirs(generateResource.map { outFile.get().asFile.parentFile })
}

for (task in listOf("dist", "docs", "samples", "verifySamples", "localSamples", "release")) {
    project.tasks.register(task) {
    }
}
