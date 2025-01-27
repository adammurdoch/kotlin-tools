import net.rubygrapefruit.plugins.app.Versions

plugins {
    id("net.rubygrapefruit.bootstrap.gradle-plugin")
}

dependencies {
    testImplementation(Versions.test.coordinates)
}
