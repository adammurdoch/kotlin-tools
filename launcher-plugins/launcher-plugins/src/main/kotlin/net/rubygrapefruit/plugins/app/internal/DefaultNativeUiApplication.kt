package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.NativeUIApplication
import org.gradle.api.model.ObjectFactory
import javax.inject.Inject

abstract class DefaultNativeUiApplication @Inject constructor(factory: ObjectFactory) : DefaultUiApplication(),
    MutableNativeApplication, NativeUIApplication {
    override val distribution: DefaultDistribution = factory.newInstance(DefaultDistribution::class.java)

    override val canBuildDistributionForHostMachine: Boolean
        get() = true
}