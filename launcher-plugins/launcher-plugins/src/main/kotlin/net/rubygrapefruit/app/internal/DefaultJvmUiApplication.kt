package net.rubygrapefruit.app.internal

import net.rubygrapefruit.app.JvmUiApplication
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import javax.inject.Inject

abstract class DefaultJvmUiApplication @Inject constructor(factory: ObjectFactory) : DefaultUiApplication(),
    MutableJvmApplication, JvmUiApplication {

    override val distribution: DefaultJvmDistribution = factory.newInstance(DefaultJvmDistribution::class.java)

    override var packaging: JvmApplicationPackaging = JvmApplicationWithEmbeddedJvm()

    override val outputModulePath: ConfigurableFileCollection = factory.fileCollection()

    override val outputModuleNames: ListProperty<String> = factory.listProperty(String::class.java)
}