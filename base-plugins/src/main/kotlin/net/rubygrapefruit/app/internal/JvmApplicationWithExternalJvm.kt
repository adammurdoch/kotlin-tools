package net.rubygrapefruit.app.internal

class JvmApplicationWithExternalJvm : JvmApplicationWithLauncherScripts {
    override val includeRuntimeModules: Boolean
        get() = true
}