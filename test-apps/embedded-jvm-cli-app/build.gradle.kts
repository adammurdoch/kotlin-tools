plugins {
    id("net.rubygrapefruit.jvm-cli-app").version("1.0")
    id("net.rubygrapefruit.embedded-jvm").version("1.0")
}

application {
    module.set("sample")
    mainClass.set("sample.MainKt")
}
