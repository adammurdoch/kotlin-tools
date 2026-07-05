package net.rubygrapefruit.plugins.app.internal.component

import org.gradle.api.Project
import kotlin.reflect.KClass
import kotlin.reflect.cast

open class ComponentRegistry(private val project: Project) {
    private val actions = mutableListOf<ApplyAction<*>>()
    private var main: MutableComponent? = null

    fun register(component: MutableComponent) {
        if (main != null) {
            throw UnsupportedOperationException("Support for multiple components in the same project is not implemented.")
        }
        main = component
        project.afterEvaluate {
            realize(component)
            actions.clear()
        }
    }

    fun <T : Any> applyToProject(type: KClass<T>, action: (T) -> Unit) {
        actions.add(ApplyAction(type, action))
    }

    inline fun <reified T : Any> applyToProject(noinline action: (T) -> Unit) {
        applyToProject(T::class, action)
    }

    private fun realize(component: MutableComponent) {
        for (action in actions) {
            action.maybeApplyToProject(component)
        }
    }

    private class ApplyAction<T : Any>(val type: KClass<T>, val action: (T) -> Unit) {
        fun maybeApplyToProject(component: Any) {
            if (type.isInstance(component)) {
                action(type.cast(component))
            }
        }
    }
}