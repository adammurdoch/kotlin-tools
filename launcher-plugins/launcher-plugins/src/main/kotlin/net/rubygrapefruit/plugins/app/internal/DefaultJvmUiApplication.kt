package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.JvmUiApplication
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.model.ObjectFactory
import javax.inject.Inject

abstract class DefaultJvmUiApplication @Inject constructor(factory: ObjectFactory) : DefaultUiApplication(),
    MutableJvmApplication, JvmUiApplication {

    override val distribution: DefaultDistribution = factory.newInstance(DefaultDistribution::class.java)

    override var packaging: JvmApplicationPackaging = JvmApplicationWithEmbeddedJvm()

    override val runtimeModulePath: ConfigurableFileCollection = factory.fileCollection()

}