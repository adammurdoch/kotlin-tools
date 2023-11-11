plugins {
    id("net.rubygrapefruit.kmp.base-lib")
}

group = "net.rubygrapefruit.libs"

library {
    jvm()
    nativeDesktop()
}

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