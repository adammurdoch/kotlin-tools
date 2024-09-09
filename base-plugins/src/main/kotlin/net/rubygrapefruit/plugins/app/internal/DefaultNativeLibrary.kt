package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.NativeLibrary
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler
import javax.inject.Inject

abstract class DefaultNativeLibrary @Inject constructor(
    private val project: Project,
    private val mainSourceSetName: String
) : NativeLibrary {
    override fun dependencies(config: KotlinDependencyHandler.() -> Unit) {
        project.extensions.getByType(KotlinProjectExtension::class.java).sourceSets.getByName(mainSourceSetName).dependencies { config() }
    }
}