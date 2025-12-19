import net.rubygrapefruit.plugins.app.Versions

plugins {
    id("net.rubygrapefruit.bootstrap.gradle-plugin")
    id("net.rubygrapefruit.plugins.stage2.serialization")
}

dependencies {
    testImplementation(Versions.test.coordinates)
    implementation(Versions.serialization.json.coordinates)
}
