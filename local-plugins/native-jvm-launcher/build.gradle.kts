import net.rubygrapefruit.plugins.app.BuildType.Release
import net.rubygrapefruit.plugins.app.internal.RealizedNativeExecutable

plugins {
    id("net.rubygrapefruit.native.cli-app")
}

group = versions.plugins.group

application {
    macOS {
        dependencies {
            implementation(project(":native-launcher"))
        }
    }
}

componentRegistry.each<RealizedNativeExecutable> {
    derive { executable ->
        if (executable.canBuild && executable.buildType == Release) {
            configurations.create("outgoingNativeBinary${executable.machine.name}") {
                attributes.attribute(Usage.USAGE_ATTRIBUTE, objects.named("native-binary-${executable.machine.kotlinTarget}"))
                isCanBeResolved = false
                isCanBeConsumed = true
                outgoing.artifact(executable.binaryFile)
            }
        }
    }
}
