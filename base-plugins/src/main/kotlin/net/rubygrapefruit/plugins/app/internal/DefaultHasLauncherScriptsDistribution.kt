package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.BuildType
import net.rubygrapefruit.plugins.app.Distribution
import net.rubygrapefruit.plugins.app.internal.tasks.DistributionImage
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider
import javax.inject.Inject

abstract class DefaultHasLauncherScriptsDistribution @Inject constructor(
    name: String,
    canBuildForHostMachine: Boolean,
    buildType: BuildType,
    distTask: TaskProvider<DistributionImage>,
    defaultDist: Provider<Distribution>,
    factory: ObjectFactory
) : DefaultPlatformIndependentDistribution(name, canBuildForHostMachine, buildType, distTask, defaultDist, factory), HasLauncherScripts