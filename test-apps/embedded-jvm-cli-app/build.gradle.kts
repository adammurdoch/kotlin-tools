plugins {
    id("net.rubygrapefruit.jvm.cli-app")
    id("net.rubygrapefruit.jvm.embedded-jvm")
}

application {
    mainClass.set("sample.MainKt")
}
