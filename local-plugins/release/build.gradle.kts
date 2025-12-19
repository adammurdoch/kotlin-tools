import net.rubygrapefruit.plugins.app.Versions

plugins {
    id("net.rubygrapefruit.bootstrap.gradle-plugin")
    id("net.rubygrapefruit.plugins.stage2.serialization")
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
