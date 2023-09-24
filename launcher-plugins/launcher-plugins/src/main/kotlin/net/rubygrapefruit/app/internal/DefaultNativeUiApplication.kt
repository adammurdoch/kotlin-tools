package net.rubygrapefruit.app.internal

import net.rubygrapefruit.app.NativeUIApplication
import net.rubygrapefruit.plugins.app.internal.DefaultDistribution
import net.rubygrapefruit.plugins.app.internal.MutableNativeApplication
import org.gradle.api.model.ObjectFactory
import javax.inject.Inject

abstract class DefaultNativeUiApplication @Inject constructor(factory: ObjectFactory) : DefaultUiApplication(),
    MutableNativeApplication, NativeUIApplication {
    override val distribution: DefaultDistribution = factory.newInstance(DefaultDistribution::class.java)
}