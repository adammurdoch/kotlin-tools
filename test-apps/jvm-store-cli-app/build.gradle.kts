plugins {
    id("net.rubygrapefruit.jvm.cli-app")
    id("org.jetbrains.kotlin.plugin.serialization")
}

application {
    dependencies {
        implementation("net.rubygrapefruit.libs:cli:1.0")
        implementation("net.rubygrapefruit.libs:file-io:1.0")
        implementation("net.rubygrapefruit.libs:store:1.0")
    }
}