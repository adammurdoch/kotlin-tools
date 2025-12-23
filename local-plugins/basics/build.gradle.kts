plugins {
    id("net.rubygrapefruit.stage2.kmp.lib")
    id("net.rubygrapefruit.bootstrap.samples")
    id("net.rubygrapefruit.bootstrap.release")
}

library {
    jvm {
        targetJvmVersion = buildConstants.plugins.jvm.version
    }
}

component {
    description = "A collection of general purpose utilities for Kotlin multiplatform"
    nextVersion = "0.0.2-milestone-1"
}
