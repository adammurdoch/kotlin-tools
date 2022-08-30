plugins {
    id("net.rubygrapefruit.native.cli-app")
}

val nativeBinary = application.outputBinary
configurations.create("outgoingNativeBinary") {
    attributes.attribute(Usage.USAGE_ATTRIBUTE, objects.named("native-binary"))
    isCanBeResolved = false
    isCanBeConsumed = true
    outgoing.artifact(nativeBinary)
}
