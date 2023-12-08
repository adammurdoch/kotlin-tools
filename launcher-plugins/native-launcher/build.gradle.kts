import net.rubygrapefruit.plugins.bootstrap.Versions

plugins {
    id("net.rubygrapefruit.native.base-cli-app")
}

group = Versions.pluginsGroup

application {
    macOS()
}

for (executable in application.executables) {
    if (executable.canBuild) {
        configurations.create("outgoingNativeBinary${executable.targetMachine.name}") {
            attributes.attribute(Usage.USAGE_ATTRIBUTE, objects.named("native-binary-${executable.targetMachine.kotlinTarget}"))
            isCanBeResolved = false
            isCanBeConsumed = true
            outgoing.artifact(executable.outputBinary)
        }
    }
}
