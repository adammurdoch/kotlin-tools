package net.rubygrapefruit.app.internal

import net.rubygrapefruit.app.NativeUIApplication
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ProviderFactory
import javax.inject.Inject

abstract class DefaultNativeUiApplication @Inject constructor(
    factory: ObjectFactory, providers: ProviderFactory
) : DefaultNativeApplication(factory, providers), NativeUIApplication {
}