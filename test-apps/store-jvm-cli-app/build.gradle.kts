plugins {
    id("net.rubygrapefruit.jvm.cli-app")
    id("org.jetbrains.kotlin.plugin.serialization")
}

application {
    dependencies {
        implementation("net.rubygrapefruit:cli-app:1.0")
        implementation("net.rubygrapefruit:file-io:1.0")
        implementation("net.rubygrapefruit:store:1.0")
    }
}