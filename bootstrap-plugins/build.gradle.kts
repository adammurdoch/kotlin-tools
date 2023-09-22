plugins {
    id("org.jetbrains.kotlin.jvm").version("1.9.10")
    id("java-gradle-plugin")
}

object Versions {
    val kotlin = "1.9.10"
    val serialization = "1.9.10"
    val serializationJson = "1.5.1"
    val java = 17
    val pluginsJava = 11

    val kotlinPluginCoordinates
        get() = "org.jetbrains.kotlin:kotlin-gradle-plugin:${kotlin}"

    val pluginsGroup = "net.rubygrapefruit.plugins"
    val bootstrapPluginCoordinates
        get() = "$pluginsGroup:bootstrap-plugins:0.1-dev"
}

group = Versions.pluginsGroup

repositories {
    mavenCentral()
}

dependencies {
    api(Versions.kotlinPluginCoordinates)
}

kotlin {
    jvmToolchain(Versions.pluginsJava)
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
            
            object Versions {
                val kotlin = "${Versions.kotlin}"
                val serialization = "${Versions.serialization}"
                val serializationJson = "${Versions.serializationJson}"
                val java = ${Versions.java}
                val pluginsJava = ${Versions.pluginsJava}
                val kotlinPluginCoordinates = "${Versions.kotlinPluginCoordinates}"
                val bootstrapPluginCoordinates = "${Versions.bootstrapPluginCoordinates}"
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
