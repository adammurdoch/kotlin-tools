package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.internal.tasks.DistributionImage
import org.gradle.api.tasks.TaskProvider
import javax.inject.Inject

abstract class DefaultHasLauncherScriptsDistribution @Inject constructor(
    name: String,
    canBuildForHostMachine: Boolean,
    distTask: TaskProvider<DistributionImage>
) : DefaultDistributionWithImage(name, canBuildForHostMachine, distTask), HasLauncherScripts
