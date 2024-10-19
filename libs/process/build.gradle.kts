import net.rubygrapefruit.plugins.app.Versions
import net.rubygrapefruit.plugins.app.internal.HostMachine

plugins {
    id("net.rubygrapefruit.kmp.base-lib")
}

group = "net.rubygrapefruit.libs"

library {
    nativeDesktop()
    common {
        api(project(":file-io"))
        implementation(project(":stream-io"))
    }
    test {
        implementation(Versions.test.coordinates)
        implementation(project(":file-fixtures"))
    }
}

val config = configurations.create("testBinary") {
    val host = HostMachine.current
    attributes.attribute(Usage.USAGE_ATTRIBUTE, objects.named("native-binary-${host.machine.kotlinTarget}"))
    isCanBeResolved = true
    isCanBeConsumed = false
}
val testApp = config.elements.map { elements -> elements.first().asFile.absolutePath }

val outFile = layout.buildDirectory.file("generated-src/commonTest/kotlin/net/rubygrapefruit/process/TestApp.kt")
val generateResource = tasks.register("generateTestResource") {
    outputs.file(outFile)
    inputs.property("path", testApp)
    doLast {
        outFile.get().asFile.writeText(
            """
            package net.rubygrapefruit.process
            
            /*
             * THIS IS A GENERATED FILE. DO NOT EDIT.
             */
            object TestApp {
                val path = "${testApp.get().replace("\\", "\\\\")}"
            }
        """.trimIndent()
        )
    }
}

kotlin {
    sourceSets.getByName("commonTest").kotlin.srcDirs(generateResource.map { outFile.get().asFile.parentFile })
}

dependencies {
    add("testBinary", project(":process-test"))
}
