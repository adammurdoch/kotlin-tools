plugins {
    id("net.rubygrapefruit.native.desktop-lib")
}

group = "net.rubygrapefruit.libs"

kotlin {
    jvm {
        jvmToolchain(17)
    }
    sourceSets {
        named("commonMain") {
            dependencies {
                api(kotlin("test"))
                api(project(":file-io"))
            }
        }
    }
}
