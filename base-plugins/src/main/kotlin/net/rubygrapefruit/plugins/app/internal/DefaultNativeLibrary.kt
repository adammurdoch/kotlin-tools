package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.NativeLibrary
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import javax.inject.Inject

abstract class DefaultNativeLibrary @Inject constructor(
    private val project: Project,
    private val mainSourceSetName: String
) : NativeLibrary {
    override fun dependencies(config: NativeLibrary.Dependencies.() -> Unit) {
        val dependencies = DependenciesImpl()
        config(dependencies)

        val sourceSets = project.extensions.getByType(KotlinProjectExtension::class.java).sourceSets
        sourceSets.whenObjectAdded { sourceSet ->
            if (sourceSet.name == mainSourceSetName) {
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
    }

    private class DependenciesImpl : NativeLibrary.Dependencies {
        val api = mutableListOf<Any>()
        val implementation = mutableListOf<Any>()

        override fun api(dependencyNotation: Any) {
            api.add(dependencyNotation)
        }

        override fun implementation(dependencyNotation: Any) {
            implementation.add(dependencyNotation)
        }
    }
}