package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.JvmComponent
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler
import javax.inject.Inject

abstract class DefaultJvmComponent @Inject constructor(
    private val project: Project,
    private val mainSourceSetName: String
) : JvmComponent {
    override fun dependencies(config: KotlinDependencyHandler.() -> Unit) {
        project.extensions.getByType(KotlinProjectExtension::class.java).sourceSets.getByName(mainSourceSetName).dependencies { config() }
    }

    override fun test(config: KotlinDependencyHandler.() -> Unit) {
        project.extensions.getByType(KotlinProjectExtension::class.java).sourceSets.getByName("test").dependencies { config() }
    }
}