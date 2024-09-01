package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.BuildType
import net.rubygrapefruit.plugins.app.internal.tasks.DistributionImage
import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.TaskProvider
import javax.inject.Inject

abstract class DefaultPlatformIndependentDistribution @Inject constructor(
    name: String,
    isDefault: Boolean,
    canBuildForHostMachine: Boolean,
    buildType: BuildType,
    distTask: TaskProvider<DistributionImage>,
    factory: ObjectFactory
) : DefaultDistribution(name, isDefault, canBuildForHostMachine, null, buildType, distTask, factory)
