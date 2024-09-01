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

    val executable: Provider<NativeExecutable> = providers.provider { targetInfo(HostMachine.current.machine, BuildType.Debug)?.executable }

    fun add(machine: NativeMachine, buildTypes: List<BuildType>, canBuildOnThisHost: Boolean = true) {
        for (buildType in buildTypes) {
            if (targetInfo(machine, buildType) == null) {
                val hostCanBuildTarget = HostMachine.current.canBuild(machine)
                val executable = DefaultNativeExecutable(machine, buildType, hostCanBuildTarget, objects)
                val distribution = distributions.add(
                    machine.kotlinTarget + buildType.name,
                    hostCanBuildTarget && HostMachine.current.canBeBuilt && HostMachine.current.machine == machine && (buildType == BuildType.Debug || buildTypes.size == 1),
                    canBuildOnThisHost && hostCanBuildTarget,
                    machine,
                    buildType
                )
                distribution.launcherFile.set(executable.outputBinary)
                machines.add(TargetInfo(machine, buildType, executable, distribution))
            }
        }
    }

    private fun targetInfo(machine: NativeMachine, buildType: BuildType) = machines.all.find { it.machine == machine && it.buildType == buildType }

    fun attachExecutable(machine: NativeMachine, buildType: BuildType, binaryFile: Provider<RegularFile>) {
        val executable = targetInfo(machine, buildType)!!.executable
        if (HostMachine.current.canBuild(machine)) {
            executable.outputBinary.set(binaryFile)
        }
    }

    fun configureTarget(machine: NativeMachine, buildType: BuildType, action: DefaultDistribution.() -> Unit) {
        action(targetInfo(machine, buildType)!!.distribution)
    }

    fun eachTarget(action: (NativeMachine, DefaultDistribution) -> Unit) {
        machines.each { action(it.machine, it.distribution) }
    }

    private class TargetInfo(
        val machine: NativeMachine,
        val buildType: BuildType,
        val executable: DefaultNativeExecutable,
        val distribution: DefaultDistribution
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