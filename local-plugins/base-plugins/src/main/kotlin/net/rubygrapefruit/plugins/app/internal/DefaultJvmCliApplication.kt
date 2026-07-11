package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.Dependencies
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ProviderFactory
import javax.inject.Inject

abstract class DefaultJvmCliApplication @Inject constructor(
    objects: ObjectFactory,
    providers: ProviderFactory,
    project: Project
) : DefaultJvmComponent<Dependencies>("test"), MutableJvmApplication {
    override val distributionContainer = DistributionContainer(project.tasks, objects, providers)
    override val runtimeModulePath: ConfigurableFileCollection = objects.fileCollection()

    override val main: HasDependencies = DefaultSourceSet("main", generatedSource)

    override fun dependencies(config: Dependencies.() -> Unit) {
        main.dependencies.config()
    }
}