package net.rubygrapefruit.app

import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider

interface NativeApplication : Application {
    /**
     * The output binary for the host machine.
     */
    val outputBinary: Provider<RegularFile>

    /**
     * The output binary for the given machine, if buildable.
     */
    fun outputBinary(target: NativeMachine): Provider<RegularFile>
}