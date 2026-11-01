plugins {
    id("net.rubygrapefruit.jvm.cli-app")
}

application {
    dependencies {
        implementation(versions.libs.coordinates("cli-app"))
    }
}