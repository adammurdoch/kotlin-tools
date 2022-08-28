package net.rubygrapefruit.app.internal

import net.rubygrapefruit.app.JvmCliApplication
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.model.ObjectFactory
import javax.inject.Inject

internal abstract class DefaultJvmCliApplication @Inject constructor(factory: ObjectFactory) : DefaultCliApplication(factory), JvmCliApplication {
    override val outputModulePath: ConfigurableFileCollection = factory.fileCollection()
}