package net.rubygrapefruit.plugins.app.internal.component

import org.gradle.api.Project

open class ComponentRegistry(private val project: Project) {
    private var main: MutableComponent? = null

    fun register(component: MutableComponent) {
        if (main != null) {
            throw UnsupportedOperationException("Support for multiple components in the same project is not implemented.")
        }
        main = component
        project.afterEvaluate {
            realize(component)
        }
    }

    private fun realize(main: MutableComponent) {
    }
}