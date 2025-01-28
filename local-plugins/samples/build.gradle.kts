import net.rubygrapefruit.plugins.app.Versions

plugins {
    id("net.rubygrapefruit.bootstrap.gradle-plugin")
    kotlin("plugin.serialization")
}

gradlePlugin {
    plugins {
        create("samples") {
            id = "net.rubygrapefruit.bootstrap.samples"
            implementationClass = "net.rubygrapefruit.plugins.samples.internal.SamplesPlugin"
        }
    }
}

dependencies {
    implementation(gradleTestKit())
    implementation(Versions.serialization.json.coordinates)
    implementation(project(":model"))
}
