plugins {
    id("net.rubygrapefruit.mpp.lib")
}

group = "net.rubygrapefruit.libs"

kotlin {
    sourceSets {
        named("commonTest") {
            dependencies {
                implementation(kotlin("test"))
                implementation(project(":file-fixtures"))
            }
        }
    }
}