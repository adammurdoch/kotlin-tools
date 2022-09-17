import net.rubygrapefruit.app.NativeMachine

plugins {
    id("net.rubygrapefruit.native.cli-app")
}

for (machine in listOf(NativeMachine.MacOSX64, NativeMachine.MacOSArm64)) {
    configurations.create("outgoingNativeBinary${machine.name}") {
        attributes.attribute(Usage.USAGE_ATTRIBUTE, objects.named("native-binary-${machine.kotlinTarget}"))
        isCanBeResolved = false
        isCanBeConsumed = true
        val binary = application.outputBinary(machine)
        if (binary.isPresent) {
            outgoing.artifact(binary)
        }
    }
}
