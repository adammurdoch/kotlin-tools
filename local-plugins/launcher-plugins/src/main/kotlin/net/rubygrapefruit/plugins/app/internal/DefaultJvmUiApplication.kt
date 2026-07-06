package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.Dependencies
import net.rubygrapefruit.plugins.app.JvmUiApplication
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ProviderFactory
import javax.inject.Inject

abstract class DefaultJvmUiApplication @Inject constructor(
    objects: ObjectFactory,
    providers: ProviderFactory,
    project: Project
) : DefaultUiApplication(objects, providers, project), MutableJvmApplication, JvmUiApplication, HasDependencies, HasGeneratedSource, HasTests {
    override val test: HasDependencies = DefaultHasDependencies("test")
    override val runtimeModulePath: ConfigurableFileCollection = objects.fileCollection()
    override val dependencies = DefaultDependencies()

    override val sourceSetName: String
        get() = "main"

    override fun dependencies(config: Dependencies.() -> Unit) {
        dependencies.config()
    }

    override fun test(config: Dependencies.() -> Unit) {
        test.dependencies.config()
    }
}