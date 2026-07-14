package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.BuildType
import net.rubygrapefruit.plugins.app.NativeExecutable
import net.rubygrapefruit.plugins.app.NativeMachine
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider

internal class DefaultNativeExecutable(
    override val targetMachine: NativeMachine,
    override val buildType: BuildType,
    override val canBuild: Boolean,
    override val outputBinary: Provider<RegularFile>
) : NativeExecutable
