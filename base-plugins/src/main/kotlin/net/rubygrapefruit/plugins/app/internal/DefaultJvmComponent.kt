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
    override fun dependencies(config: D.() -> Unit) {
        project.extensions.getByType(KotlinProjectExtension::class.java).sourceSets.getByName(mainSourceSetName).dependencies { config(wrap(this)) }
    }

    override fun test(config: D.() -> Unit) {
        project.extensions.getByType(KotlinProjectExtension::class.java).sourceSets.getByName("test").dependencies { config(wrap(this)) }
    }

    abstract fun wrap(dependencyHandler: KotlinDependencyHandler): D

}