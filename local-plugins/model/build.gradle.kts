import net.rubygrapefruit.plugins.app.Versions

plugins {
    id("net.rubygrapefruit.bootstrap.gradle-plugin")
    kotlin("plugin.serialization")
}

dependencies {
    testImplementation(Versions.test.coordinates)
    implementation(Versions.serialization.json.coordinates)
}
