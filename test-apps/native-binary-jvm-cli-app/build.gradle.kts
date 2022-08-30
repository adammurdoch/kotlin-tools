plugins {
    id("net.rubygrapefruit.jvm.cli-app")
    id("net.rubygrapefruit.jvm.native-binary")
}

application {
    module.set("sample")
    mainClass.set("sample.MainKt")
}
