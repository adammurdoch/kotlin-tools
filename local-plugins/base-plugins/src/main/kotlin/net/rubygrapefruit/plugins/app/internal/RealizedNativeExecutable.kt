package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.BuildType
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.plugin.mpp.Executable

class RealizedNativeExecutable(
    val canBuildOnHost: Boolean,
    val machine: KotlinTargetMachine,
    val buildType: BuildType,
    val executable: Executable,
    val entryPoint: Provider<String>,
    val binaryFile: Provider<RegularFile>
)
