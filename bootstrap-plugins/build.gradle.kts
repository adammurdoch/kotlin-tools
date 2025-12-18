plugins {
    id("net.rubygrapefruit.plugins.stage0.build-constants")
    id("net.rubygrapefruit.plugins.stage1.gradle-plugin")
}

object Constants {
    val dateTime = "0.7.1"
    val io = "0.8.0"
    val java = 17
    val pluginsJava = 11

    val pluginsGroup = "net.rubygrapefruit.plugins"
}

group = Constants.pluginsGroup

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    api(buildConstants.kotlin.plugin.coordinates)
    api(buildConstants.serialization.plugin.coordinates)
    implementation(buildConstants.foojay.plugin.coordinates)
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
        create("jni-lib") {
            id = "net.rubygrapefruit.bootstrap.jni.lib"
            implementationClass = "net.rubygrapefruit.plugins.bootstrap.JniLibraryPlugin"
        }
        create("kmp-lib") {
            id = "net.rubygrapefruit.bootstrap.kmp.lib"
            implementationClass = "net.rubygrapefruit.plugins.bootstrap.KmpLibraryPlugin"
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
                val java = ${Constants.java}
                val plugins = Plugins
                val libs = Libraries
            }

            object Kotlin {
                val version = "${buildConstants.kotlin.version}"
                val pluginCoordinates = "${buildConstants.kotlin.plugin.coordinates}"
            }

            object KotlinTest {
                val version = "${buildConstants.kotlin.version}"
                val coordinates = "org.jetbrains.kotlin:kotlin-test:" + version
                val junit = KotlinTestJunit
            }

            object KotlinTestJunit {
                val coordinates = "org.jetbrains.kotlin:kotlin-test-junit:${buildConstants.kotlin.version}"
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
                val coordinates = "org.jetbrains.kotlinx:kotlinx-serialization-core:${buildConstants.serialization.library.version}"
                val pluginCoordinates = "${buildConstants.serialization.plugin.coordinates}"
                val pluginId = "org.jetbrains.kotlin.plugin.serialization"
                val json = SerializationJson
            }
            
            object SerializationJson {
                val version = "${buildConstants.serialization.library.version}"
                val coordinates = "org.jetbrains.kotlinx:kotlinx-serialization-json:${buildConstants.serialization.library.version}"
            }

            object Coroutines {
                val version = "${buildConstants.coroutines.version}"
                val coordinates = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${buildConstants.coroutines.version}"
            }

            object DateTime {
                val version = "${Constants.dateTime}"
                val coordinates = "org.jetbrains.kotlinx:kotlinx-datetime:${Constants.dateTime}"
            }

            object IO {
                val version = "${buildConstants.io.version}"
                val coordinates = "org.jetbrains.kotlinx:kotlinx-io-core:${buildConstants.io.version}"
            }

            object Ksp {
                val version = "${buildConstants.ksp.version}"
                val coordinates = "com.google.devtools.ksp:symbol-processing-api:${buildConstants.ksp.version}"
                val pluginCoordinates = "com.google.devtools.ksp:com.google.devtools.ksp.gradle.plugin:${buildConstants.ksp.version}"
                val pluginId = "com.google.devtools.ksp"
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
