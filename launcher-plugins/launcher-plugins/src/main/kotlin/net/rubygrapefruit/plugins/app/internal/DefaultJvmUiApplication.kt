package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.JvmUiApplication
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.model.ObjectFactory
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler
import javax.inject.Inject

abstract class DefaultJvmUiApplication @Inject constructor(factory: ObjectFactory, private val project: Project) :
    DefaultUiApplication(),
    MutableJvmApplication, JvmUiApplication {

    override val distribution: DefaultDistribution = factory.newInstance(DefaultDistribution::class.java)

    override var packaging: JvmApplicationPackaging = JvmApplicationWithEmbeddedJvm()

    override val runtimeModulePath: ConfigurableFileCollection = factory.fileCollection()

    override fun dependencies(config: KotlinDependencyHandler.() -> Unit) {
        project.jvmKotlin.sourceSets.getByName("main").dependencies { config() }
    }

    override fun test(config: KotlinDependencyHandler.() -> Unit) {
        project.jvmKotlin.sourceSets.getByName("test").dependencies { config() }
    }

}