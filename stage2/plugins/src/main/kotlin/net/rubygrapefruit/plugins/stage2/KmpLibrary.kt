package net.rubygrapefruit.plugins.stage2

abstract class KmpLibrary(val jvm: JvmLibrary) {
    fun jvm(config: JvmLibrary.() -> Unit) {
        jvm.config()
    }
}