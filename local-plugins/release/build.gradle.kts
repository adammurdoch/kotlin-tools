import net.rubygrapefruit.plugins.app.Versions

plugins {
    id("net.rubygrapefruit.bootstrap.gradle-plugin")
}

group = Versions.plugins.group

gradlePlugin {
    plugins {
        create("release") {
            id = "net.rubygrapefruit.bootstrap.release"
            implementationClass = "net.rubygrapefruit.plugins.release.ReleasePlugin"
        }
    }
}
