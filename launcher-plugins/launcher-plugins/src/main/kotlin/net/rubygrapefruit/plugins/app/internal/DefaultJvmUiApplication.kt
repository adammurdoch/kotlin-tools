package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.JvmUiApplication
import net.rubygrapefruit.plugins.app.NativeMachine
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

    override var packaging: JvmApplicationPackaging = JvmApplicationWithEmbeddedJvm()

    override val runtimeModulePath: ConfigurableFileCollection = objects.fileCollection()

    init {
        targets.add(NativeMachine.MacOSArm64, HostMachine.current.machine == NativeMachine.MacOSArm64)
        targets.add(NativeMachine.MacOSX64, HostMachine.current.machine == NativeMachine.MacOSX64)
    }

    override fun dependencies(config: KotlinDependencyHandler.() -> Unit) {
        project.jvmKotlin.sourceSets.getByName("main").dependencies { config() }
    }

    override fun test(config: KotlinDependencyHandler.() -> Unit) {
        project.jvmKotlin.sourceSets.getByName("test").dependencies { config() }
    }
}