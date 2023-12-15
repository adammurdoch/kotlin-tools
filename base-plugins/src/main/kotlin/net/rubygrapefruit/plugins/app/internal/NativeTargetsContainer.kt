package net.rubygrapefruit.plugins.app.internal

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

    val executable: Provider<NativeExecutable> = providers.provider { targetInfo(HostMachine.current.machine)?.executable }

    fun add(machine: NativeMachine, canBuildOnThisHost: Boolean = true) {
        if (targetInfo(machine) == null) {
            val executable = DefaultNativeExecutable(machine, HostMachine.current.canBuild(machine), objects)
            val distribution = distributions.add(
                machine.kotlinTarget,
                machine == HostMachine.current.machine,
                canBuildOnThisHost && HostMachine.current.canBuild(machine)
            )
            distribution.launcherFile.set(executable.outputBinary)
            machines.add(TargetInfo(machine, executable, distribution))
        }
    }

    private fun targetInfo(machine: NativeMachine) = machines.all.find { it.machine == machine }

    fun attachExecutable(machine: NativeMachine, binaryFile: Provider<RegularFile>) {
        val executable = targetInfo(machine)!!.executable
        if (HostMachine.current.canBuild(machine)) {
            executable.outputBinary.set(binaryFile)
        }
    }

    fun configureTarget(machine: NativeMachine, action: DefaultDistribution.() -> Unit) {
        action(targetInfo(machine)!!.distribution)
    }

    fun eachTarget(action: (NativeMachine, DefaultDistribution) -> Unit) {
        machines.each { action(it.machine, it.distribution) }
    }

    private class TargetInfo(
        val machine: NativeMachine,
        val executable: DefaultNativeExecutable,
        val distribution: DefaultDistribution
    )

    private class DefaultNativeExecutable(
        override val targetMachine: NativeMachine,
        override val canBuild: Boolean,
        objectFactory: ObjectFactory
    ) : NativeExecutable {
        override val outputBinary: RegularFileProperty = objectFactory.fileProperty()
    }
}