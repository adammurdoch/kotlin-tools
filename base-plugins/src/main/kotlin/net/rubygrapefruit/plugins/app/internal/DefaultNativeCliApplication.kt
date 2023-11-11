package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.NativeApplication
import net.rubygrapefruit.plugins.app.NativeMachine
import org.gradle.api.Project
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler
import javax.inject.Inject

abstract class DefaultNativeCliApplication @Inject constructor(
    factory: ObjectFactory,
    private val providers: ProviderFactory,
    private val project: Project
) : MutableApplication, MutableNativeApplication, NativeApplication {
    private val outputs = mutableMapOf<NativeMachine, Provider<RegularFile>>()

    override val distribution: DefaultDistribution = factory.newInstance(DefaultDistribution::class.java)

    override val outputBinary: RegularFileProperty = factory.fileProperty()

    override fun outputBinary(target: NativeMachine): Provider<RegularFile> {
        return outputs[target] ?: providers.provider { null }
    }

    fun addOutputBinary(machine: NativeMachine, binaryFile: Provider<RegularFile>, currentHost: Boolean) {
        if (currentHost) {
            outputBinary.set(binaryFile)
        }
        outputs[machine] = binaryFile
    }

    override fun common(config: KotlinDependencyHandler.() -> Unit) {
        project.kotlin.sourceSets.getByName("commonMain").dependencies { config() }
    }
}
