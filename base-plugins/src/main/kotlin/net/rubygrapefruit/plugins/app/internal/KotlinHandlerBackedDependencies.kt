package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.Dependencies
import net.rubygrapefruit.plugins.app.LibraryDependencies
import org.gradle.api.Project
import org.gradle.api.artifacts.ExternalModuleDependency
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler

open class KotlinHandlerBackedDependencies(protected val dependencyHandler: KotlinDependencyHandler) : Dependencies {
    override fun implementation(dependencyNotation: String) {
        dependencyHandler.implementation(dependencyNotation)
    }

    override fun implementation(dependencyNotation: ExternalModuleDependency) {
        dependencyHandler.implementation(dependencyNotation)
    }

    override fun implementation(dependencyNotation: Project) {
        dependencyHandler.implementation(dependencyNotation)
    }
}

class KotlinHandlerBackedLibraryDependencies(dependencyHandler: KotlinDependencyHandler) : KotlinHandlerBackedDependencies(dependencyHandler), LibraryDependencies {
    override fun api(dependencyNotation: String) {
        dependencyHandler.api(dependencyNotation)
    }

    override fun api(dependencyNotation: ExternalModuleDependency) {
        dependencyHandler.api(dependencyNotation)
    }

    override fun api(dependencyNotation: Project) {
        dependencyHandler.api(dependencyNotation)
    }
}