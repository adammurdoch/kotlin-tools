package net.rubygrapefruit.plugins.stage2

sealed class DerivedAppBuilder {
}

class DerivedJvmCliAppBuilder : DerivedAppBuilder() {
    fun launcher(name: String) {
    }

    fun requiresJvm(version: Int) {
    }

    fun embeddedJvm() {
    }

    fun nativeBinaries() {
    }
}

class DerivedNativeCliAppBuilder: DerivedAppBuilder() {
    fun launcher(name: String) {
    }
}