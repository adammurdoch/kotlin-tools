plugins {
    id("net.rubygrapefruit.jvm.cli-app")
}

application {
    appName.set("app")
    module.set("sample.app")
    mainClass.set("sample.app.MainKt")
}
