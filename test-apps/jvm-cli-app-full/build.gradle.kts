plugins {
    id("net.rubygrapefruit.jvm.cli-app")
}

application {
    dependencies {
        implementation("com.github.ajalt.clikt:clikt:4.2.1")
    }
}