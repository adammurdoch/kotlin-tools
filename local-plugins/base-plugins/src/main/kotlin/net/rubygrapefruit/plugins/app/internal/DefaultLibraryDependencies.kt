package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.LibraryDependencies
import org.gradle.api.Project
import org.gradle.api.artifacts.ExternalModuleDependency
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler

class DefaultLibraryDependencies : DefaultDependencies(), LibraryDependencies {
    private val api = mutableListOf<Any>()

    override fun api(dependencyNotation: String) {
        api.add(dependencyNotation)
    }

    override fun api(dependencyNotation: ExternalModuleDependency) {
        api.add(dependencyNotation)
    }

    override fun api(dependencyNotation: Project) {
        api.add(dependencyNotation)
    }

    override fun applyTo(handler: KotlinDependencyHandler) {
        super.applyTo(handler)
        for (notation in api) {
            handler.api(notation)
        }
    }
}