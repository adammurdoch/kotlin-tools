import net.rubygrapefruit.plugins.app.Versions

plugins {
    id("net.rubygrapefruit.bootstrap.jvm.lib")
    id("net.rubygrapefruit.stage2.serialization")
}

dependencies {
    implementation(gradleApi())
    implementation(Versions.kotlin.pluginCoordinates)
    testImplementation(Versions.test.coordinates)
}
