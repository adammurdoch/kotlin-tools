package net.rubygrapefruit.app.internal

interface JvmApplicationPackaging {
    val includeRuntimeModules: Boolean
        get() = false
}