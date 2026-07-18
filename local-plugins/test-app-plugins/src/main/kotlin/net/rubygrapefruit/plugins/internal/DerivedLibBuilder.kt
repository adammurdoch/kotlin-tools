package net.rubygrapefruit.plugins.internal

class DerivedJvmLibBuilder(val name: String) {
    internal var jvmVersion = 17
        private set

    fun requiresJvm(version: Int) {
        jvmVersion = version
    }
}