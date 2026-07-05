package net.rubygrapefruit.plugins.app.internal.component

import net.rubygrapefruit.plugins.app.internal.SourceSets
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import kotlin.reflect.KClass
import kotlin.reflect.cast

open class ComponentRegistry(private val project: Project) {
    private val deriveActions = mutableListOf<DeriveAction<*>>()
    private val applyActions = mutableListOf<ApplyAction<*>>()
    private var main: MutableComponent? = null

    fun register(component: MutableComponent) {
        if (main != null) {
            throw UnsupportedOperationException("Support for multiple root components in the same project is not implemented.")
        }
        main = component
        project.afterEvaluate {
            val sourceSets = SourceSets(project)
            val context = DefaultContext(sourceSets) { value, context -> realize(value, context) }
            context.derive(component)
            context.realize()
//            applyActions.clear()
//            deriveActions.clear()
        }
    }

    fun <T : Any> deriveFrom(type: KClass<T>, action: Context.(T) -> Unit) {
        deriveActions.add(DeriveAction(type, action))
    }

    inline fun <reified T : Any> deriveFrom(noinline action: Context.(T) -> Unit) {
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

    private fun realize(component: Any, context: Context) {
        for (action in deriveActions) {
            action.maybeDerive(component, context)
        }
        for (action in applyActions) {
            action.maybeApplyToProject(component)
        }
    }

    private class DeriveAction<T : Any>(val type: KClass<T>, val action: Context.(T) -> Unit) {
        fun maybeDerive(component: Any, context: Context) {
            if (type.isInstance(component)) {
                context.action(type.cast(component))
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

    interface Context {
        fun derive(value: Any)

        fun deriveNullable(value: Any?) {
            if (value != null) {
                derive(value)
            }
        }

        fun deriveFromSourceSet(name: String, action: Context.(KotlinSourceSet) -> Unit)
    }

    private class DefaultContext(val sourceSets: SourceSets, val realizeAction: (Any, Context) -> Unit) : Context {
        private val queue = mutableListOf<Any>()
        private var realizing = false

        fun realize() {
            if (realizing) {
                return
            }
            realizing = true
            while (queue.isNotEmpty()) {
                val value = queue.removeFirst()
                realizeAction(value, this)
            }
            realizing = false
        }

        override fun derive(value: Any) {
            queue.add(value)
        }

        override fun deriveFromSourceSet(name: String, action: Context.(KotlinSourceSet) -> Unit) {
            sourceSets.withSourceSet(name) { sourceSet, _ ->
                action(sourceSet)
                realize()
            }
        }
    }
}