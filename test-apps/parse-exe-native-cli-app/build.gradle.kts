plugins {
    id("net.rubygrapefruit.native.cli-app")
}

application {
    entryPoint = "sample.main"
    common {
        implementation("net.rubygrapefruit:cli-app:1.0")
        implementation("net.rubygrapefruit:file-io:1.0")
        implementation("net.rubygrapefruit:parse:1.0")
    }
}