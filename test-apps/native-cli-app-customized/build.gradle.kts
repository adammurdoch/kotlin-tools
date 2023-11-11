plugins {
    id("net.rubygrapefruit.native.cli-app")
}

application {
    appName = "app"
}

kotlin {
    sourceSets {
        named("commonMain") {
            dependencies {
                implementation(project(":kmp-lib-customized"))
                implementation(project(":native-lib"))
            }
        }
    }
}
