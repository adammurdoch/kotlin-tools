package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.BuildType
import net.rubygrapefruit.plugins.app.NativeMachine
import net.rubygrapefruit.plugins.app.internal.tasks.ReleaseDistribution
import org.gradle.api.tasks.TaskProvider
import javax.inject.Inject

abstract class DefaultReleaseDistribution @Inject constructor(
    name: String,
    canBuildOnHostMachine: Boolean,
    override val targetMachine: NativeMachine,
    override val buildType: BuildType,
    override val distTask: TaskProvider<ReleaseDistribution>
) : DefaultMutableDistribution(name, canBuildOnHostMachine), BuildableDistribution, HasTargetMachine {
}