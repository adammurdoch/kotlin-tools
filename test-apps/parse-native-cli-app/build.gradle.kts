plugins {
    id("net.rubygrapefruit.native.cli-app")
}

application {
    entryPoint = "sample.main"
    common {
        implementation(project(":parse-kmp-lib"))
    }
}
