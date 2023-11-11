package net.rubygrapefruit.plugins.app

import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler

interface NativeApplication : Application {
    /**
     * The output binary for the host machine.
     */
    val outputBinary: Provider<RegularFile>

    /**
     * The output binary for the given machine, if buildable.
     */
    fun outputBinary(target: NativeMachine): Provider<RegularFile>

    /**
     * Configures common dependencies.
     */
    fun common(config: KotlinDependencyHandler.() -> Unit)
}