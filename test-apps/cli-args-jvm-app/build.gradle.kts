plugins {
    id("net.rubygrapefruit.jvm.cli-app")
}

application {
    appName = "cli-args-app"
    dependencies {
        implementation(versions.libs.coordinates("cli-app"))
    }
}