package net.rubygrapefruit.app.internal

import net.rubygrapefruit.app.NativeCliApplication
import net.rubygrapefruit.app.NativeMachine
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import javax.inject.Inject

internal abstract class DefaultNativeCliApplication @Inject constructor(factory: ObjectFactory, private val providers: ProviderFactory) : NativeCliApplication {
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
}
