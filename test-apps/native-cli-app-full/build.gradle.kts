import net.rubygrapefruit.plugins.app.Versions

plugins {
    id("net.rubygrapefruit.native.cli-app")
    id("org.jetbrains.kotlin.plugin.serialization")
}

application {
    entryPoint = "sample.main"
    common {
        implementation(Versions.coroutines.coordinates)
        implementation("com.github.ajalt.clikt:clikt:4.2.1")
        implementation("net.rubygrapefruit.libs:file-io:1.0")
        implementation("net.rubygrapefruit.libs:store:1.0")
    }
}