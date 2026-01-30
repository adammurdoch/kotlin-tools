plugins {
    id("net.rubygrapefruit.jvm.cli-app")
}

application {
    dependencies {
        implementation("net.rubygrapefruit:cli-app:1.0")
        implementation("net.rubygrapefruit:stream-io:1.0")
        implementation(project(":parse-kmp-lib"))
        implementation(project(":kmp-lib-render"))
    }
}