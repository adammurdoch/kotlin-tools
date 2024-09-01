package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.BuildType
import net.rubygrapefruit.plugins.app.JvmApplication
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ProviderFactory
import javax.inject.Inject

abstract class DefaultJvmCliApplication @Inject constructor(
    objects: ObjectFactory,
    providers: ProviderFactory,
    project: Project
) :
    DefaultJvmComponent(project, "main"), MutableApplication, MutableJvmApplication, JvmApplication {

    final override val distributionContainer = DistributionContainer(project.tasks, objects, providers)

    override var packaging: JvmApplicationPackaging = JvmApplicationWithExternalJvm()

    override val runtimeModulePath: ConfigurableFileCollection = objects.fileCollection()

    init {
        distributionContainer.add("main", true, true, null, BuildType.Release)
    }
}