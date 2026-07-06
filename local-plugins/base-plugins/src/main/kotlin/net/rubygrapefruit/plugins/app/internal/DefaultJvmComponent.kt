package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.Dependencies
import net.rubygrapefruit.plugins.app.JvmComponent
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler
import javax.inject.Inject

abstract class DefaultJvmComponent<D : Dependencies> @Inject constructor(
    private val project: Project,
    private val mainSourceSetName: String,
    private val testSourceSetName: String
) : JvmComponent<D>, HasGeneratedSource, HasTests {
    override val test: HasDependencies = object : HasDependencies {
        override val sourceSetName: String
            get() = testSourceSetName
        override val dependencies = DefaultDependencies()
    }

    override val sourceSetName: String
        get() = mainSourceSetName

    override fun dependencies(config: D.() -> Unit) {
        val sourceSet = project.extensions.getByType(KotlinProjectExtension::class.java).sourceSets.getByName(mainSourceSetName)
        sourceSet.dependencies { config(wrap(this)) }
    }

    override fun test(config: Dependencies.() -> Unit) {
        test.dependencies.config()
    }

    abstract fun wrap(dependencyHandler: KotlinDependencyHandler): D

}