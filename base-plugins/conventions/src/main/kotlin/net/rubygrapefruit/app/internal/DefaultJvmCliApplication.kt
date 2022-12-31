package net.rubygrapefruit.app.internal

import net.rubygrapefruit.app.JvmApplication
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import javax.inject.Inject

abstract class DefaultJvmCliApplication @Inject constructor(factory: ObjectFactory) : MutableApplication,
    MutableJvmApplication, JvmApplication {
    override val distribution: DefaultJvmDistribution = factory.newInstance(DefaultJvmDistribution::class.java)

    override var packaging: JvmApplicationPackaging = JvmApplicationWithExternalJvm()

    override val outputModulePath: ConfigurableFileCollection = factory.fileCollection()

    override val outputModuleNames: ListProperty<String> = factory.listProperty(String::class.java)
}