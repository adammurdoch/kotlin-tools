plugins {
    id("net.rubygrapefruit.jvm-cli-app")
    id("net.rubygrapefruit.native-binary")
}

application {
    module.set("sample")
    mainClass.set("sample.MainKt")
}
