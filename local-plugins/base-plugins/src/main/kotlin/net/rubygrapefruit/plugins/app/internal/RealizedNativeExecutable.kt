package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.BuildType
import net.rubygrapefruit.plugins.app.NativeMachine
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.plugin.mpp.Executable

class RealizedNativeExecutable(
    val canBuildOnHost: Boolean,
    val machine: NativeMachine,
    val buildType: BuildType,
    val executable: Executable,
    val binaryFile: Provider<RegularFile>
)
