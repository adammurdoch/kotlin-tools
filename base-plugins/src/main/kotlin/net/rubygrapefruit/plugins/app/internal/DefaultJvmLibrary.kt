package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.JvmLibrary
import net.rubygrapefruit.plugins.app.LibraryDependencies
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler
import javax.inject.Inject

abstract class DefaultJvmLibrary @Inject constructor(
    project: Project,
    mainSourceSetName: String
) : DefaultJvmComponent<LibraryDependencies>(project, mainSourceSetName), JvmLibrary {
    override fun wrap(dependencyHandler: KotlinDependencyHandler): LibraryDependencies {
        return KotlinHandlerBackedLibraryDependencies(dependencyHandler)
    }
}