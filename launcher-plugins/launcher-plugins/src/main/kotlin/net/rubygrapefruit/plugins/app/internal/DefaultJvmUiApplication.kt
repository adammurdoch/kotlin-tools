package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.Dependencies
import net.rubygrapefruit.plugins.app.JvmUiApplication
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ProviderFactory
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler
import javax.inject.Inject

abstract class DefaultJvmUiApplication @Inject constructor(
    objects: ObjectFactory,
    providers: ProviderFactory,
    private val project: Project
) : DefaultUiApplication(objects, providers, project), MutableJvmApplication, JvmUiApplication {

    override val runtimeModulePath: ConfigurableFileCollection = objects.fileCollection()

    override fun dependencies(config: Dependencies.() -> Unit) {
        project.jvmKotlin.sourceSets.getByName("main").dependencies { config(KotlinHandlerBackedDependencies(this)) }
    }

    override fun test(config: Dependencies.() -> Unit) {
        project.jvmKotlin.sourceSets.getByName("test").dependencies { config(KotlinHandlerBackedDependencies(this)) }
    }
}