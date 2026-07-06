package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.Dependencies
import org.gradle.api.Project
import org.gradle.api.artifacts.ExternalModuleDependency
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler

open class DefaultDependencies : Dependencies {
    val implementation = mutableListOf<Any>()

    override fun implementation(dependencyNotation: String) {
        implementation.add(dependencyNotation)
    }

    override fun implementation(dependencyNotation: ExternalModuleDependency) {
        implementation.add(dependencyNotation)
    }

    override fun implementation(dependencyNotation: Project) {
        implementation.add(dependencyNotation)
    }

    open fun applyTo(handler: KotlinDependencyHandler) {
        for (notation in implementation) {
            handler.implementation(notation)
        }
    }
}