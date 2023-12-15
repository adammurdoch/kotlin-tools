package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.NativeApplication
import net.rubygrapefruit.plugins.app.NativeExecutable
import net.rubygrapefruit.plugins.app.NativeMachine
import org.gradle.api.Project
import org.gradle.api.file.RegularFile
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

    private val targets = NativeTargetsContainer(objects, providers, project.tasks)

    override val distributionContainer = targets.distributions

    override val executables: Provider<List<NativeExecutable>>
        get() = targets.executables

    override val executable: Provider<NativeExecutable>
        get() = targets.executable

    override fun macOS() {
        componentRegistry.macOS { register(it) }
    }

    override fun nativeDesktop() {
        componentRegistry.desktop { register(it) }
    }

    private fun KotlinNativeBinaryContainer.register(target: NativeMachine) {
        executable()
        targets.add(target)
    }

    fun attachExecutable(machine: NativeMachine, binaryFile: Provider<RegularFile>) {
        targets.attachExecutable(machine, binaryFile)
    }

    fun configureTarget(machine: NativeMachine, action: DefaultDistribution.() -> Unit) {
        targets.configureTarget(machine, action)
    }

    override fun common(config: KotlinDependencyHandler.() -> Unit) {
        project.kotlin.sourceSets.getByName("commonMain").dependencies { config() }
    }

    override fun test(config: KotlinDependencyHandler.() -> Unit) {
        project.kotlin.sourceSets.getByName("commonTest").dependencies { config() }
    }
}
