package net.rubygrapefruit.app.internal

import net.rubygrapefruit.app.JvmUiApplication
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.model.ObjectFactory
import javax.inject.Inject

abstract class DefaultJvmUiApplication @Inject constructor(factory: ObjectFactory) : DefaultUiApplication(),
    MutableJvmApplication, JvmUiApplication {

    override val distribution: DefaultJvmDistribution = factory.newInstance(DefaultJvmDistribution::class.java)

    override var packaging: JvmApplicationPackaging = JvmApplicationWithEmbeddedJvm()

    override val runtimeModulePath: ConfigurableFileCollection = factory.fileCollection()

}