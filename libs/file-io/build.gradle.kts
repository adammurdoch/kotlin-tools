plugins {
    id("net.rubygrapefruit.native.desktop-lib")
}

group = "net.rubygrapefruit.libs"

kotlin {
    jvm {
        jvmToolchain(17)
    }
    sourceSets {
        named("commonTest") {
            dependencies {
                implementation(kotlin("test"))
                implementation(project(":file-fixtures"))
            }
        }
    }
}