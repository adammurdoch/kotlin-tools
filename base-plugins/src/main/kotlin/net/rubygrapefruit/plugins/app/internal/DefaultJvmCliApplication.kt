package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.JvmApplication
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.model.ObjectFactory
import javax.inject.Inject

abstract class DefaultJvmCliApplication @Inject constructor(factory: ObjectFactory, project: Project) :
    DefaultJvmComponent(project), MutableApplication, MutableJvmApplication, JvmApplication {
    override val distribution: DefaultDistribution = factory.newInstance(DefaultDistribution::class.java)

    override val canBuildDistributionForHostMachine: Boolean
        get() = HostMachine.current is MacOS

    override var packaging: JvmApplicationPackaging = JvmApplicationWithExternalJvm()

    override val runtimeModulePath: ConfigurableFileCollection = factory.fileCollection()

}