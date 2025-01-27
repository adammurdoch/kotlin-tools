import net.rubygrapefruit.plugins.app.Versions

plugins {
    id("net.rubygrapefruit.bootstrap.gradle-plugin")
}

group = Versions.plugins.group

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
    implementation(project(":model"))
}
