package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.BuildType
import net.rubygrapefruit.plugins.app.NativeExecutable
import net.rubygrapefruit.plugins.app.NativeMachine
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.tasks.TaskContainer

class NativeTargetsContainer(
    private val objects: ObjectFactory,
    providers: ProviderFactory,
    tasks: TaskContainer
) {
    private val machines = SimpleContainer<TargetInfo>()

    val distributions = DistributionContainer(tasks, objects, providers)

    val executables: Provider<List<NativeExecutable>> = providers.provider { machines.all.map { it.executable } }

    fun <T : MutableDistribution> add(machine: NativeMachine, buildTypes: List<BuildType>, distributionType: Class<T>, canBuildOnThisHost: Boolean = true) {
        for (buildType in buildTypes) {
            val targetInfo = targetInfo(machine, buildType)
            if (targetInfo != null) {
                throw IllegalArgumentException("Target for $machine already registered")
            }

            val hostCanBuildTarget = HostMachine.current.canBuild(machine)
            val defaultMachine = hostCanBuildTarget && HostMachine.current.canBeBuilt && HostMachine.current.machine == machine
            val executable = DefaultNativeExecutable(machine, buildType, hostCanBuildTarget, objects)
            val distribution = distributions.add(
                buildType.name,
                defaultMachine && (buildType == BuildType.Debug || buildTypes.size == 1),
                defaultMachine && (buildType == BuildType.Release || buildTypes.size == 1),
                canBuildOnThisHost && hostCanBuildTarget,
                machine,
                buildType,
                distributionType
            )
            distribution.launcherFile.set(executable.outputBinary)
            machines.add(TargetInfo(machine, buildType, executable, distribution))
        }
    }

    private fun targetInfo(machine: NativeMachine, buildType: BuildType) = machines.all.find { it.machine == machine && it.buildType == buildType }

    fun attachExecutable(machine: NativeMachine, buildType: BuildType, binaryFile: Provider<RegularFile>) {
        val executable = targetInfo(machine, buildType)!!.executable
        if (HostMachine.current.canBuild(machine)) {
            executable.outputBinary.set(binaryFile)
        }
    }

    fun configureTarget(machine: NativeMachine, buildType: BuildType, action: MutableDistribution.() -> Unit) {
        action(targetInfo(machine, buildType)!!.distribution)
    }

    private class TargetInfo(
        val machine: NativeMachine,
        val buildType: BuildType,
        val executable: DefaultNativeExecutable,
        val distribution: MutableDistribution
    )

    private class DefaultNativeExecutable(
        override val targetMachine: NativeMachine,
        override val buildType: BuildType,
        override val canBuild: Boolean,
        objectFactory: ObjectFactory
    ) : NativeExecutable {
        override val outputBinary: RegularFileProperty = objectFactory.fileProperty()
    }
}