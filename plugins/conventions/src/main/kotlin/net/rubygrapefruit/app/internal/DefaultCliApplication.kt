package net.rubygrapefruit.app.internal

import net.rubygrapefruit.app.CliApplication
import net.rubygrapefruit.app.Distribution
import org.gradle.api.model.ObjectFactory
import javax.inject.Inject

internal abstract class DefaultCliApplication : CliApplication {
    @get:Inject
    abstract val factory: ObjectFactory

    fun setup() {
        distribution.set(factory.newInstance(Distribution::class.java))
    }
}