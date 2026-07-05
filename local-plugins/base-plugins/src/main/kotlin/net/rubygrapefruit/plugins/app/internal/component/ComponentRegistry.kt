package net.rubygrapefruit.plugins.app.internal.component

import org.gradle.api.Project
import kotlin.reflect.KClass
import kotlin.reflect.cast

open class ComponentRegistry(private val project: Project) {
    private val deriveActions = mutableListOf<DeriveAction<*>>()
    private val applyActions = mutableListOf<ApplyAction<*>>()
    private var main: MutableComponent? = null

    fun register(component: MutableComponent) {
        if (main != null) {
            throw UnsupportedOperationException("Support for multiple components in the same project is not implemented.")
        }
        main = component
        project.afterEvaluate {
            val queue = mutableListOf<Any>()
            queue.add(component)
            while (!queue.isEmpty()) {
                val value = queue.removeFirst()
                realize(value, queue)
            }
            applyActions.clear()
        }
    }

    fun <T : Any> deriveFrom(type: KClass<T>, action: (T) -> List<Any>) {
        deriveActions.add(DeriveAction(type, action))
    }

    inline fun <reified T : Any> deriveFrom(noinline action: (T) -> List<Any>) {
        deriveFrom(T::class, action)
    }

    /**
     * Applies a configured object of type [T] to the project.
     */
    fun <T : Any> applyToProject(type: KClass<T>, action: (T) -> Unit) {
        applyActions.add(ApplyAction(type, action))
    }

    /**
     * Applies a configured object of type [T] to the project.
     */
    inline fun <reified T : Any> applyToProject(noinline action: (T) -> Unit) {
        applyToProject(T::class, action)
    }

    private fun realize(component: Any, queue: MutableList<Any>) {
        for (action in deriveActions) {
            action.maybeDeriveInto(component, queue)
        }
        for (action in applyActions) {
            action.maybeApplyToProject(component)
        }
    }

    private class DeriveAction<T : Any>(val type: KClass<T>, val action: (T) -> List<Any>) {
        fun maybeDeriveInto(component: Any, target: MutableList<Any>) {
            if (type.isInstance(component)) {
                val result = action(type.cast(component))
                target.addAll(result)
            }
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