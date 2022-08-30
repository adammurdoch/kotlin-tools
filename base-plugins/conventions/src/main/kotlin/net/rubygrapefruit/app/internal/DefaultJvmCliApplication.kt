package net.rubygrapefruit.app.internal

import net.rubygrapefruit.app.JvmCliApplication
import net.rubygrapefruit.app.JvmDistribution
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.model.ObjectFactory
import javax.inject.Inject

internal abstract class DefaultJvmCliApplication @Inject constructor(factory: ObjectFactory) : JvmCliApplication {
    override val distribution: JvmDistribution = factory.newInstance(JvmDistribution::class.java)

    override val outputModulePath: ConfigurableFileCollection = factory.fileCollection()
}