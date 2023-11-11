plugins {
    id("net.rubygrapefruit.kmp.base-lib")
}

group = "net.rubygrapefruit.libs"

library {
    jvm()
    nativeDesktop()
}

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