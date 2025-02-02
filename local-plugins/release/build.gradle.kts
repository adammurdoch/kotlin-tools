import net.rubygrapefruit.plugins.app.Versions

plugins {
    id("net.rubygrapefruit.bootstrap.gradle-plugin")
    kotlin("plugin.serialization")
}

gradlePlugin {
    plugins {
        create("release") {
            id = "net.rubygrapefruit.bootstrap.release"
            implementationClass = "net.rubygrapefruit.plugins.release.internal.ReleasePlugin"
        }
    }
}

dependencies {
    implementation(project(":model"))
    implementation(Versions.serialization.json.coordinates)
}
