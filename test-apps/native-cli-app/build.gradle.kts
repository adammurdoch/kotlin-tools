plugins {
    id("net.rubygrapefruit.native.cli-app")
}

kotlin {
    sourceSets {
        named("commonMain") {
            dependencies {
                implementation(project(":kmp-lib"))
                implementation(project(":native-lib"))
            }
        }
    }
}
