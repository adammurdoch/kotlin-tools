plugins {
    id("net.rubygrapefruit.jvm.cli-app")
}

application {
    dependencies {
        implementation("net.rubygrapefruit:cli-app:1.0")
        implementation("net.rubygrapefruit:file-io:1.0")
        implementation("net.rubygrapefruit:parse:1.0")
    }
}