package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.NativeApplication
import net.rubygrapefruit.plugins.app.NativeExecutable
import net.rubygrapefruit.plugins.app.NativeMachine
import org.gradle.api.Project
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import org.jetbrains.kotlin.gradle.dsl.KotlinNativeBinaryContainer
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler
import javax.inject.Inject

abstract class DefaultNativeCliApplication @Inject constructor(
    private val componentRegistry: MultiPlatformComponentRegistry,
    private val objects: ObjectFactory,
    providers: ProviderFactory,
    private val project: Project
) : MutableApplication, MutableNativeApplication, NativeApplication {

    private val targets = mutableMapOf<NativeMachine, TargetInfo>()

    override val distributionContainer = DistributionContainer(project.tasks, objects, providers)

    override val executables: Provider<List<NativeExecutable>> = providers.provider { targets.values.map { it.executable } }

    override val executable: Provider<NativeExecutable> = providers.provider { targets[HostMachine.current.machine]?.executable }

    override fun macOS() {
        componentRegistry.macOS { register(it) }
    }

    override fun nativeDesktop() {
        componentRegistry.desktop { register(it) }
    }

    private fun KotlinNativeBinaryContainer.register(target: NativeMachine) {
        executable()
        if (!targets.containsKey(target)) {
            val executable = DefaultNativeExecutable(target, HostMachine.current.canBuild(target), objects)
            val distribution = distributionContainer.add(
                target.kotlinTarget,
                target == HostMachine.current.machine,
                HostMachine.current.canBuild(target)
            )
            distribution.launcherFile.set(executable.outputBinary)
            targets[target] = TargetInfo(executable, distribution)
        }
    }

    fun addOutputExecutable(machine: NativeMachine, binaryFile: Provider<RegularFile>) {
        val executable = targets.getValue(machine).executable
        if (HostMachine.current.canBuild(machine)) {
            executable.outputBinary.set(binaryFile)
        }
    }

    fun configureTarget(machine: NativeMachine, action: DefaultDistribution.() -> Unit) {
        action(targets.getValue(machine).distribution)
    }

    override fun common(config: KotlinDependencyHandler.() -> Unit) {
        project.kotlin.sourceSets.getByName("commonMain").dependencies { config() }
    }

    override fun test(config: KotlinDependencyHandler.() -> Unit) {
        project.kotlin.sourceSets.getByName("commonTest").dependencies { config() }
    }

    private class TargetInfo(
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
