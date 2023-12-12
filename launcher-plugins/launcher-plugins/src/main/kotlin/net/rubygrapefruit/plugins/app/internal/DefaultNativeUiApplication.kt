package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.NativeUIApplication
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ProviderFactory
import javax.inject.Inject

abstract class DefaultNativeUiApplication @Inject constructor(objects: ObjectFactory, providers: ProviderFactory) : DefaultUiApplication(objects, providers),
    MutableNativeApplication, NativeUIApplication {
}