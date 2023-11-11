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
        named("commonMain") {
            dependencies {
                api(kotlin("test"))
                api(project(":file-io"))
            }
        }
    }
}
