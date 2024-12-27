plugins {
    id("net.rubygrapefruit.native.cli-app")
    id("org.jetbrains.kotlin.plugin.serialization")
}

application {
    entryPoint = "sample.main"
    common {
        implementation("net.rubygrapefruit.libs:cli-app:1.0")
        implementation("net.rubygrapefruit.libs:file-io:1.0")
        implementation("net.rubygrapefruit.libs:store:1.0")
    }
}
