package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.LibraryDependencies
import net.rubygrapefruit.plugins.app.NativeLibrary
import org.gradle.api.Project
import org.gradle.api.artifacts.ExternalModuleDependency
import javax.inject.Inject

abstract class DefaultNativeLibrary @Inject constructor(
    private val sourceSets: SourceSets,
    private val mainSourceSetName: String
) : NativeLibrary {
    override fun dependencies(config: LibraryDependencies.() -> Unit) {
        val dependencies = DependenciesImpl()
        config(dependencies)

        sourceSets.withSourceSet(mainSourceSetName) { sourceSet, _ ->
            sourceSet.dependencies {
                for (notation in dependencies.api) {
                    api(notation)
                }
                for (notation in dependencies.implementation) {
                    implementation(notation)
                }
            }
        }
    }

    fun attach() {
        sourceSets.withSourceSet(mainSourceSetName) { sourceSet, _ ->
            sourceSets.withSourceSet(mainSourceSetName) { mainSourceSet, _ ->
                mainSourceSet.kotlin.srcDirs(generatedSource)
            }
        }
    }

    private class DependenciesImpl : LibraryDependencies {
        val api = mutableListOf<Any>()
        val implementation = mutableListOf<Any>()

        override fun api(dependencyNotation: String) {
            api.add(dependencyNotation)
        }

        override fun api(dependencyNotation: ExternalModuleDependency) {
            api.add(dependencyNotation)
        }

        override fun api(dependencyNotation: Project) {
            api.add(dependencyNotation)
        }

        override fun implementation(dependencyNotation: String) {
            implementation.add(dependencyNotation)
        }

        override fun implementation(dependencyNotation: ExternalModuleDependency) {
            implementation.add(dependencyNotation)
        }

        override fun implementation(dependencyNotation: Project) {
            implementation.add(dependencyNotation)
        }
    }
}