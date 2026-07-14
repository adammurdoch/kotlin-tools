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

    fun add(machine: NativeMachine, buildTypes: List<BuildType>) {
        for (buildType in buildTypes) {
            val targetInfo = targetInfo(machine, buildType)
            if (targetInfo != null) {
                throw IllegalArgumentException("Target for $machine already registered")
            }

            val hostCanBuildTarget = HostMachine.current.canBuild(machine)
            val executable = DefaultNativeExecutable(machine, buildType, hostCanBuildTarget, objects)
            machines.add(TargetInfo(machine, buildType, executable))
        }
    }

    private fun targetInfo(machine: NativeMachine, buildType: BuildType) = machines.all.find { it.machine == machine && it.buildType == buildType }

    fun attachExecutable(machine: NativeMachine, buildType: BuildType, binaryFile: Provider<RegularFile>) {
        val executable = targetInfo(machine, buildType)!!.executable
        if (HostMachine.current.canBuild(machine)) {
            executable.outputBinary.set(binaryFile)
        }
    }

    private class TargetInfo(
        val machine: NativeMachine,
        val buildType: BuildType,
        val executable: DefaultNativeExecutable
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