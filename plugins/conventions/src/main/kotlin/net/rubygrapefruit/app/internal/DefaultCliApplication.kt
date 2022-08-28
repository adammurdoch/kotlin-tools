package net.rubygrapefruit.app.internal

import net.rubygrapefruit.app.CliApplication
import net.rubygrapefruit.app.Distribution
import org.gradle.api.model.ObjectFactory
import javax.inject.Inject

internal abstract class DefaultCliApplication @Inject constructor(factory: ObjectFactory) : CliApplication {
    override val distribution: Distribution = factory.newInstance(Distribution::class.java)
}
