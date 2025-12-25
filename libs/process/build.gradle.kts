import net.rubygrapefruit.plugins.app.internal.HostMachine

plugins {
    id("net.rubygrapefruit.kmp.base-lib")
}

group = versions.libs.group

library {
    nativeDesktop()
    common {
        api(project(":file-io"))
        implementation(project(":stream-io"))
    }
    test {
        implementation(versions.test.coordinates)
        implementation(project(":file-fixtures"))
    }
}

val config = configurations.create("testBinary") {
    val host = HostMachine.current
    attributes.attribute(Usage.USAGE_ATTRIBUTE, objects.named("native-binary-${host.machine.kotlinTarget}"))
    isCanBeResolved = true
    isCanBeConsumed = false
}

/*
 * Generate a source file containing the location of the test application.
 */
val testApp = config.elements.map { elements -> elements.first().asFile.absolutePath }
val outFile = layout.buildDirectory.file("generated-src/commonTest/kotlin/net/rubygrapefruit/process/TestApp.kt")
val generateResource = tasks.register("generateTestResource", GenerateTestResource::class.java) {
    outputFile = outFile
    testPath = testApp
}

kotlin {
    sourceSets.getByName("commonTest").kotlin.srcDirs(generateResource.map { outFile.get().asFile.parentFile })
}

dependencies {
    add("testBinary", project(":process-test"))
}

abstract class GenerateTestResource : DefaultTask() {
    @get:Input
    abstract val testPath: Property<String>

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    @TaskAction
    fun generate() {
        outputFile.get().asFile.writeText(
            """
            package net.rubygrapefruit.process
            
            /*
             * THIS IS A GENERATED FILE. DO NOT EDIT.
             */
            object TestApp {
                val path = "${testPath.get().replace("\\", "\\\\")}"
            }
        """.trimIndent()
        )
    }
}