package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.BuildType
import net.rubygrapefruit.plugins.app.Distribution
import net.rubygrapefruit.plugins.app.NativeMachine
import net.rubygrapefruit.plugins.app.internal.tasks.DistributionImage
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider
import javax.inject.Inject

abstract class DefaultNativeUiAppDistribution @Inject constructor(
    name: String,
    canBuildForHostMachine: Boolean,
    target: NativeMachine,
    buildType: BuildType,
    distTask: TaskProvider<DistributionImage>,
    defaultDist: Provider<Distribution>,
    factory: ObjectFactory
) : DefaultHasLauncherExecutableDistribution(name, canBuildForHostMachine, target, buildType, distTask, defaultDist, factory), HasUnsignedUiBundle
