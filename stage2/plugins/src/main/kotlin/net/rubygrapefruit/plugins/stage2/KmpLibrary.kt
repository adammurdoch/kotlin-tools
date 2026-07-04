package net.rubygrapefruit.plugins.stage2

abstract class KmpLibrary(val jvm: JvmLibrary) {
    fun jvm(config: JvmLibrary.() -> Unit) {
        jvm.config()
    }

    fun nativeDesktop() {
        // Ignore
    }

    fun browser() {
        // Ignore
    }
}