import net.rubygrapefruit.plugins.app.BuildType.Release
import net.rubygrapefruit.plugins.app.internal.RealizedNativeExecutable

plugins {
    id("net.rubygrapefruit.native.cli-app")
}

application {
    nativeDesktop()
    common {
        implementation(project(":cli-app"))
        implementation(project(":stream-io"))
        implementation(project(":file-io"))
    }
}

componentRegistry.each<RealizedNativeExecutable> {
    derive { executable ->
        if (executable.canBuildOnHost && executable.buildType == Release) {
            configurations.create("outgoingNativeBinary${executable.machine.name}") {
                attributes.attribute(Usage.USAGE_ATTRIBUTE, objects.named("native-binary-${executable.machine.kotlinTarget}"))
                isCanBeResolved = false
                isCanBeConsumed = true
                outgoing.artifact(executable.binaryFile)
            }
        }
    }
}
