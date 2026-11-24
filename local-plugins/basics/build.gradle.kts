plugins {
    id("net.rubygrapefruit.convention.base-kmp-lib")
    id("net.rubygrapefruit.bootstrap.samples")
    id("net.rubygrapefruit.bootstrap.release")
}

library {
    jvm {
        targetJvmVersion = 11
    }
    nativeDesktop()
    browser()
}

component {
    description = "A collection of general purpose utilities for Kotlin multiplatform"
    nextVersion = "0.0.2-milestone-1"
}
