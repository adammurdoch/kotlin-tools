package net.rubygrapefruit.app.internal

import net.rubygrapefruit.app.Distribution
import net.rubygrapefruit.app.NativeCliApplication
import org.gradle.api.model.ObjectFactory
import javax.inject.Inject

internal abstract class DefaultNativeCliApplication @Inject constructor(factory: ObjectFactory) : NativeCliApplication {
    override val distribution: Distribution = factory.newInstance(Distribution::class.java)
}
