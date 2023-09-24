package net.rubygrapefruit.plugins.app.internal

class JvmApplicationWithExternalJvm : JvmApplicationWithLauncherScripts {
    override val includeRuntimeModules: Boolean
        get() = true
}