import net.rubygrapefruit.plugins.app.BuildType.Release
import net.rubygrapefruit.plugins.app.internal.RealizedNativeExecutable
import org.gradle.kotlin.dsl.support.serviceOf

plugins {
    id("net.rubygrapefruit.native.cli-app")
}

group = versions.plugins.group

application {
    macOS {
        dependencies {
            implementation(project(":ui-app-bootstrap"))
        }
    }
}

kotlin {
    macosArm64 {
        compilations.getByName("main") {
            cinterops.create("jni") {
                val includeDirs = serviceOf<JavaToolchainService>().launcherFor {
                    languageVersion = JavaLanguageVersion.of(21)
                }.map { launcher ->
                    listOf(
                        launcher.metadata.installationPath.file("include"),
                        launcher.metadata.installationPath.file("include/darwin")
                    ).also {
                        println("-> INCLUDE DIRS: $it")
                    }
                }
                includeDirs(includeDirs)
            }
        }
    }
}

componentRegistry.each<RealizedNativeExecutable> {
    derive { executable ->
        if (executable.canBuildOnHost && executable.buildType == Release) {
            configurations.create("outgoingNativeBinary${executable.machine.alias}") {
                attributes.attribute(Usage.USAGE_ATTRIBUTE, objects.named("native-binary-${executable.machine.kotlinTarget}"))
                isCanBeResolved = false
                isCanBeConsumed = true
                outgoing.artifact(executable.binaryFile)
            }
        }
    }
}
