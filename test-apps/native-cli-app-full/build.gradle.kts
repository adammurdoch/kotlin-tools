plugins {
    id("net.rubygrapefruit.native.cli-app")
}

application {
    common {
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
        implementation("com.github.ajalt.clikt:clikt:4.2.1")
    }
}