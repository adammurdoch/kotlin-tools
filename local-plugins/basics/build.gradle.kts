plugins {
    id("net.rubygrapefruit.kmp.lib")
    id("net.rubygrapefruit.bootstrap.samples")
    id("net.rubygrapefruit.bootstrap.release")
}

group = versions.libs.group

library {
    jvm {
        targetJvmVersion = 11
    }
}

component {
    description = "A collection of general purpose utilities for Kotlin multiplatform"
    nextVersion = "0.0.2-milestone-1"
}
