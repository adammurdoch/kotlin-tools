package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.BuildType
import net.rubygrapefruit.plugins.app.NativeMachine
import net.rubygrapefruit.plugins.app.internal.tasks.DistributionImage
import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.TaskProvider
import javax.inject.Inject

abstract class DefaultJvmUiAppDistribution @Inject constructor(
    name: String,
    canBuildForHostMachine: Boolean,
    target: NativeMachine,
    buildType: BuildType,
    distTask: TaskProvider<DistributionImage>,
    factory: ObjectFactory
) : DefaultHasLauncherExecutableDistribution(name, canBuildForHostMachine, target, buildType, distTask, factory), HasEmbeddedJvm, HasUnsignedUiBundle
