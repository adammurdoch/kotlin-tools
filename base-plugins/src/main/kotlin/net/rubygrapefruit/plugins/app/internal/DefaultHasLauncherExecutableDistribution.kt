package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.BuildType
import net.rubygrapefruit.plugins.app.NativeMachine
import net.rubygrapefruit.plugins.app.internal.tasks.DistributionImage
import org.gradle.api.tasks.TaskProvider
import javax.inject.Inject

abstract class DefaultHasLauncherExecutableDistribution @Inject constructor(
    name: String,
    canBuildForHostMachine: Boolean,
    override val targetMachine: NativeMachine,
    override val buildType: BuildType,
    distTask: TaskProvider<DistributionImage>
) : DefaultDistributionWithImage(name, canBuildForHostMachine, distTask), HasLauncherExecutable
