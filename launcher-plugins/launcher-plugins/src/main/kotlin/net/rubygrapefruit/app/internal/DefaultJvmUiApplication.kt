package net.rubygrapefruit.app.internal

import net.rubygrapefruit.app.JvmUiApplication
import org.gradle.api.model.ObjectFactory

abstract class DefaultJvmUiApplication(factory: ObjectFactory) : DefaultJvmApplication(factory), JvmUiApplication {
}