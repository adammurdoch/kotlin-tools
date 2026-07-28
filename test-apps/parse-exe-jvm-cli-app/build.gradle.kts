plugins {
    id("net.rubygrapefruit.jvm.cli-app")
}

application {
    dependencies {
        implementation(versions.libs.coordinates("cli-app"))
        implementation(versions.libs.coordinates("file-io"))
        implementation(versions.libs.coordinates("parse"))
    }
}