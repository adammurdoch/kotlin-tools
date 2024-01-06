plugins {
    id("net.rubygrapefruit.jvm.cli-app")
    id("org.jetbrains.kotlin.plugin.serialization")
}

application {
    dependencies {
        implementation("com.github.ajalt.clikt:clikt:4.2.1")
        implementation("net.rubygrapefruit.libs:file-io:1.0")
        implementation("net.rubygrapefruit.libs:store:1.0")
    }
}