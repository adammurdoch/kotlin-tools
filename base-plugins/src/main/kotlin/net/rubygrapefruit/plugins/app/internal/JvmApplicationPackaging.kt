package net.rubygrapefruit.plugins.app.internal

interface JvmApplicationPackaging {
    val includeRuntimeModules: Boolean
        get() = false
}