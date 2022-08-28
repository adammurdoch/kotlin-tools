plugins {
    id("net.rubygrapefruit.jvm-cli-app")
    id("net.rubygrapefruit.embedded-jvm")
}

application {
    module.set("sample")
    mainClass.set("sample.MainKt")
}
