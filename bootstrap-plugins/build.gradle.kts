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
}

group = "net.rubygrapefruit.plugins"

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
        create("bootstrap-jvm") {
            id = "net.rubygrapefruit.bootstrap.jvm"
            implementationClass = "net.rubygrapefruit.plugins.bootstrap.JvmPlugin"
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
                val pluginsJava = ${Versions.pluginsJava}
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
