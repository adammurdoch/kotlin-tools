package net.rubygrapefruit.plugins.app

import org.gradle.api.Project
import org.gradle.api.artifacts.ExternalModuleDependency

interface Dependencies {
    fun implementation(dependencyNotation: String)
    fun implementation(dependencyNotation: ExternalModuleDependency)
    fun implementation(dependencyNotation: Project)
}

interface LibraryDependencies : Dependencies {
    fun api(dependencyNotation: String)
    fun api(dependencyNotation: ExternalModuleDependency)
    fun api(dependencyNotation: Project)
}
