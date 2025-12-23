package net.rubygrapefruit.plugins.app.internal

import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

class SourceSets(
    private val project: Project
) {
    /**
     * Runs the given action when the given source set becomes available.
     */
    fun withSourceSet(name: String, action: (KotlinSourceSet, NamedDomainObjectContainer<KotlinSourceSet>) -> Unit) {
        val sourceSets = project.kotlin.sourceSets
        val current = sourceSets.findByName(name)
        if (current != null) {
            action(current, sourceSets)
        } else {
            sourceSets.whenObjectAdded { sourceSet ->
                if (sourceSet.name == name) {
                    action(sourceSet, sourceSets)
                }
            }
        }
    }
}