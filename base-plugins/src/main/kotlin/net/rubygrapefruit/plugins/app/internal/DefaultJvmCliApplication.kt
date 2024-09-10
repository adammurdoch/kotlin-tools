package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.Dependencies
import net.rubygrapefruit.plugins.app.JvmApplication
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ProviderFactory
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler
import javax.inject.Inject

abstract class DefaultJvmCliApplication @Inject constructor(
    objects: ObjectFactory,
    providers: ProviderFactory,
    project: Project
) : DefaultJvmComponent<Dependencies>(project, "main"), MutableApplication, MutableJvmApplication, JvmApplication {

    final override val distributionContainer = DistributionContainer(project.tasks, objects, providers)

    override val runtimeModulePath: ConfigurableFileCollection = objects.fileCollection()

    override fun wrap(dependencyHandler: KotlinDependencyHandler): Dependencies {
        return KotlinHandlerBackedDependencies(dependencyHandler)
    }
}