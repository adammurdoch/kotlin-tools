import net.rubygrapefruit.plugins.app.BuildType

plugins {
    id("net.rubygrapefruit.native.base-cli-app")
}

group = versions.plugins.group

application {
    macOS {
        dependencies {
            implementation(project(":native-launcher"))
        }
    }
}

for (executable in application.executables.get()) {
    if (executable.canBuild && executable.buildType == BuildType.Release) {
        configurations.create("outgoingNativeBinary${executable.targetMachine.name}") {
            attributes.attribute(Usage.USAGE_ATTRIBUTE, objects.named("native-binary-${executable.targetMachine.kotlinTarget}"))
            isCanBeResolved = false
            isCanBeConsumed = true
            outgoing.artifact(executable.outputBinary)
        }
    }
}
