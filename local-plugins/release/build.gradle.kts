plugins {
    id("net.rubygrapefruit.bootstrap.gradle-plugin")
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
}
