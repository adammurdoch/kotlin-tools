package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.Dependencies
import net.rubygrapefruit.plugins.app.JvmComponent
import javax.inject.Inject

abstract class DefaultJvmComponent<D : Dependencies> @Inject constructor(
    private val mainSourceSetName: String,
    testSourceSetName: String
) : JvmComponent<D>, HasDependencies, HasGeneratedSource, HasTests {
    override val test: HasDependencies = DefaultHasDependencies(testSourceSetName)

    override val sourceSetName: String
        get() = mainSourceSetName

    override fun test(config: Dependencies.() -> Unit) {
        test.dependencies.config()
    }
}