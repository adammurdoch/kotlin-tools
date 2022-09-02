package net.rubygrapefruit.app.internal

import net.rubygrapefruit.app.NativeCliApplication
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import javax.inject.Inject

internal abstract class DefaultNativeCliApplication @Inject constructor(factory: ObjectFactory) : NativeCliApplication {
    override val distribution: DefaultDistribution = factory.newInstance(DefaultDistribution::class.java)

    override val outputBinary: RegularFileProperty = factory.fileProperty()
}
