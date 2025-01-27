plugins {
    id("net.rubygrapefruit.bootstrap.gradle-plugin")
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
    implementation(project(":model"))
}
