package net.rubygrapefruit.plugins.app

import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider

interface NativeExecutable {
    /**
     * The target machine for this executable
     */
    val targetMachine: NativeMachine

    /**
     * Can this executable be built on the host machine?
     */
    val canBuild: Boolean

    /**
     * The output binary, if buildable on the host machine.
     */
    val outputBinary: Provider<RegularFile>
}