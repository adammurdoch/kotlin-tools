package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.Dependencies
import net.rubygrapefruit.plugins.app.NativeLibrary
import net.rubygrapefruit.plugins.app.NativeUIApplication
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ProviderFactory
import javax.inject.Inject

abstract class DefaultNativeUiApplication @Inject constructor(
    objects: ObjectFactory,
    providers: ProviderFactory,
    private val project: Project
) : DefaultUiApplication(objects, providers, project), MutableNativeApplication, NativeUIApplication {
    override fun macOS(config: NativeLibrary.() -> Unit) {
        TODO("Not yet implemented")
    }

    override fun common(config: Dependencies.() -> Unit) {
        project.kotlin.sourceSets.getByName("commonMain").dependencies { config(KotlinHandlerBackedDependencies(this)) }
    }

    override fun test(config: Dependencies.() -> Unit) {
        project.kotlin.sourceSets.getByName("commonTest").dependencies { config(KotlinHandlerBackedDependencies(this)) }
    }
}