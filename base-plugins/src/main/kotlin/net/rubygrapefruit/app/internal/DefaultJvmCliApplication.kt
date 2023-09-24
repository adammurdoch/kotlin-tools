package net.rubygrapefruit.app.internal

import net.rubygrapefruit.app.JvmApplication
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.model.ObjectFactory
import javax.inject.Inject

abstract class DefaultJvmCliApplication @Inject constructor(factory: ObjectFactory) : MutableApplication,
    MutableJvmApplication, JvmApplication {
    override val distribution: DefaultDistribution = factory.newInstance(DefaultDistribution::class.java)

    override var packaging: JvmApplicationPackaging = JvmApplicationWithExternalJvm()

    override val runtimeModulePath: ConfigurableFileCollection = factory.fileCollection()

}