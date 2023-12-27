plugins {
    id("net.rubygrapefruit.native.cli-app")
}

application {
    common {
        implementation("com.github.ajalt.clikt:clikt:4.2.1")
    }
}