package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.Dependencies
import net.rubygrapefruit.plugins.app.JvmComponent
import javax.inject.Inject

abstract class DefaultJvmComponent<D : Dependencies> @Inject constructor(
    testSourceSetName: String
) : JvmComponent<D>, PlatformContribution, HasTests {
    override val test: HasDependencies = DefaultHasDependencies(testSourceSetName)

    override fun test(config: Dependencies.() -> Unit) {
        test.dependencies.config()
    }
}