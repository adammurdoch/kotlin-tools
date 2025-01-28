plugins {
    id("org.jetbrains.kotlin.jvm").version("2.1.0")
    id("java-gradle-plugin")
}

object Constants {
    val kotlin = "2.1.0"
    val serializationPlugin = "2.1.0"
    val serializationLibrary = "1.8.0"
    val coroutines = "1.10.1"
    val dateTime = "0.6.1"
    val io = "0.6.0"
    val ksp = "2.1.0-1.0.29"
    val java = 17
    val pluginsJava = 11

    val kotlinPluginCoordinates
        get() = "org.jetbrains.kotlin:kotlin-gradle-plugin:${kotlin}"
    val serializationPluginCoordinates
        get() = "org.jetbrains.kotlin:kotlin-serialization:${serializationPlugin}"

    val pluginsGroup = "net.rubygrapefruit.plugins"
}

group = Constants.pluginsGroup

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    api(Constants.kotlinPluginCoordinates)
    api(Constants.serializationPluginCoordinates)
    implementation("org.gradle.toolchains:foojay-resolver:0.9.0")
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
                val version = "${Constants.kotlin}"
                val pluginCoordinates = "${Constants.kotlinPluginCoordinates}"
            }

            object KotlinTest {
                val version = "${Constants.kotlin}"
                val coordinates = "org.jetbrains.kotlin:kotlin-test:" + version
                val junit = KotlinTestJunit
            }

            object KotlinTestJunit {
                val coordinates = "org.jetbrains.kotlin:kotlin-test-junit:${Constants.kotlin}"
            }
            
            object Plugins {
                val java = ${Constants.pluginsJava}
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
                val version = "${Constants.serializationPlugin}"
                val coordinates = "org.jetbrains.kotlinx:kotlinx-serialization-core:${Constants.serializationLibrary}"
                val pluginCoordinates = "${Constants.serializationPluginCoordinates}"
                val pluginId = "org.jetbrains.kotlin.plugin.serialization"
                val json = SerializationJson
            }
            
            object SerializationJson {
                val version = "${Constants.serializationLibrary}"
                val coordinates = "org.jetbrains.kotlinx:kotlinx-serialization-json:${Constants.serializationLibrary}"
            }

            object Coroutines {
                val version = "${Constants.coroutines}"
                val coordinates = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Constants.coroutines}"
            }

            object DateTime {
                val version = "${Constants.dateTime}"
                val coordinates = "org.jetbrains.kotlinx:kotlinx-datetime:${Constants.dateTime}"
            }

            object IO {
                val version = "${Constants.io}"
                val coordinates = "org.jetbrains.kotlinx:kotlinx-io-core:${Constants.io}"
            }

            object Ksp {
                val version = "${Constants.ksp}"
                val coordinates = "com.google.devtools.ksp:symbol-processing-api:${Constants.ksp}"
                val pluginCoordinates = "com.google.devtools.ksp:com.google.devtools.ksp.gradle.plugin:${Constants.ksp}"
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
