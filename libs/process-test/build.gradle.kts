plugins {
    id("net.rubygrapefruit.native.cli-app")
}

application {
    common {
        implementation(project(":cli-app"))
        implementation(project(":file-io"))
    }
}

for (executable in application.executables.get()) {
    if (executable.canBuild && executable.buildType == net.rubygrapefruit.plugins.app.BuildType.Debug) {
        configurations.create("outgoingNativeBinary${executable.targetMachine.name}") {
            attributes.attribute(Usage.USAGE_ATTRIBUTE, objects.named("native-binary-${executable.targetMachine.kotlinTarget}"))
            isCanBeResolved = false
            isCanBeConsumed = true
            outgoing.artifact(executable.outputBinary)
        }
    }
}
