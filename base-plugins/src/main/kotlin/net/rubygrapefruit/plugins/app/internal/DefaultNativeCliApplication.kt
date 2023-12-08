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
    private val factory: ObjectFactory,
    private val providers: ProviderFactory,
    private val project: Project
) : MutableApplication, MutableNativeApplication, NativeApplication {
    private val executableForMachine = mutableMapOf<NativeMachine, DefaultNativeExecutable>()

    override val distribution: DefaultDistribution = factory.newInstance(DefaultDistribution::class.java)

    override val canBuildDistributionForHostMachine: Boolean
        get() = executableForMachine.containsKey(HostMachine.current.machine)

    override fun macOS() {
        componentRegistry.macOS { register(it) }
    }

    override fun nativeDesktop() {
        componentRegistry.desktop { register(it) }
    }

    private fun KotlinNativeBinaryContainer.register(target: NativeMachine) {
        executable()
        executableForMachine.computeIfAbsent(target) { DefaultNativeExecutable(target, HostMachine.current.canBuild(target), factory) }
    }

    override val executables: List<NativeExecutable>
        get() = executableForMachine.values.toList()

    override val outputBinary: Provider<RegularFile>
        get() = executableForMachine[HostMachine.current.machine]?.outputBinary ?: providers.provider { null }

    fun addOutputBinary(machine: NativeMachine, binaryFile: Provider<RegularFile>) {
        executableForMachine.getValue(machine).outputBinary.set(binaryFile)
    }

    override fun common(config: KotlinDependencyHandler.() -> Unit) {
        project.kotlin.sourceSets.getByName("commonMain").dependencies { config() }
    }

    override fun test(config: KotlinDependencyHandler.() -> Unit) {
        project.kotlin.sourceSets.getByName("commonTest").dependencies { config() }
    }

    private class DefaultNativeExecutable(
        override val targetMachine: NativeMachine,
        override val canBuild: Boolean,
        objectFactory: ObjectFactory
    ) : NativeExecutable {
        override val outputBinary: RegularFileProperty = objectFactory.fileProperty()
    }
}
