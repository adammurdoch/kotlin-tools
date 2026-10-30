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
    // This is a hack: the Kotlin plugin was attempting to download the native distributions on Linux and Windows, even though they are not targets.
    // Seems to be related to the c-interop for macOS causing some parts of the Kotlin build to be disabled, but not all of them.
    // Adding a dummy target seems to resolve this
    nativeDesktop()
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
                    )
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
