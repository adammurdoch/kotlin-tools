package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.Dependencies
import net.rubygrapefruit.plugins.app.JvmComponent
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler
import javax.inject.Inject

abstract class DefaultJvmComponent<D : Dependencies> @Inject constructor(
    private val project: Project,
    private val mainSourceSetName: String
) : JvmComponent<D> {
    private val mainSourceSet
        get() = project.extensions.getByType(KotlinProjectExtension::class.java).sourceSets.getByName(mainSourceSetName)

    override fun dependencies(config: D.() -> Unit) {
        mainSourceSet.dependencies { config(wrap(this)) }
    }

    override fun test(config: D.() -> Unit) {
        project.extensions.getByType(KotlinProjectExtension::class.java).sourceSets.getByName("test").dependencies { config(wrap(this)) }
    }

    fun attach() {
        mainSourceSet.kotlin.srcDirs(generatedSource)
    }

    abstract fun wrap(dependencyHandler: KotlinDependencyHandler): D

}