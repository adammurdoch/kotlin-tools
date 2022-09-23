plugins {
    id("net.rubygrapefruit.mpp.lib")
}

group = "net.rubygrapefruit.libs"

kotlin {
    sourceSets {
        named("commonMain") {
            dependencies {
                api(project(":file-io"))
            }
        }
    }
}
