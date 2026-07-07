package net.rubygrapefruit.plugins.app.internal.component

import net.rubygrapefruit.plugins.app.internal.SourceSets
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import kotlin.reflect.KClass
import kotlin.reflect.cast

open class ComponentRegistry(private val project: Project) {
    private val deriveActions = mutableListOf<DeriveAction<*>>()
    private val applyActions = mutableListOf<ApplyAction>()
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
        applyActions.add(ApplyToType(type, action))
    }

    /**
     * Applies a configured object of type [T] to the project.
     */
    inline fun <reified T : Any> applyToProject(noinline action: (T) -> Unit) {
        applyToProject(T::class, action)
    }

    /**
     * Applies a configured object of type [T] to the project, for those objects derived from object of type [P].
     */
    fun <P : Any, T : Any> applyToProject(parentType: KClass<P>, type: KClass<T>, action: (P, T) -> Unit) {
        applyActions.add(ApplyToTypeWithParent(parentType, type, action))
    }

    /**
     * Applies a configured object of type [T] to the project, for those objects derived from object of type [P].
     */
    inline fun <reified P : Any, reified T : Any> applyToProject(noinline action: (P, T) -> Unit) {
        applyToProject(P::class, T::class, action)
    }

    private fun realize(registration: Registration, context: Context) {
        for (action in deriveActions) {
            action.maybeDerive(registration.value, context)
        }
        for (action in applyActions) {
            action.maybeApplyToProject(registration.value, registration.parent?.value)
        }
    }

    private class DeriveAction<T : Any>(val type: KClass<T>, val action: Context.(T) -> Unit) {
        fun maybeDerive(component: Any, context: Context) {
            if (type.isInstance(component)) {
                context.action(type.cast(component))
            }
        }
    }

    private sealed class ApplyAction {
        abstract fun maybeApplyToProject(component: Any, parent: Any?)
    }

    private class ApplyToType<T : Any>(val type: KClass<T>, val action: (T) -> Unit) : ApplyAction() {
        override fun maybeApplyToProject(component: Any, parent: Any?) {
            if (type.isInstance(component)) {
                action(type.cast(component))
            }
        }
    }

    private class ApplyToTypeWithParent<P : Any, T : Any>(val parentType: KClass<P>, val type: KClass<T>, val action: (P, T) -> Unit) : ApplyAction() {
        override fun maybeApplyToProject(component: Any, parent: Any?) {
            if (parentType.isInstance(parent) && type.isInstance(component)) {
                action(parentType.cast(parent), type.cast(component))
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

    private class DefaultContext(val sourceSets: SourceSets, val realizeAction: (Registration, Context) -> Unit) : Context {
        private val queue = mutableListOf<Registration>()
        private var realizing: Registration? = null

        fun realize() {
            if (realizing != null) {
                return
            }
            while (queue.isNotEmpty()) {
                val registration = queue.removeFirst()
                realizing = registration
                realizeAction(registration, this)
            }
            realizing = null
        }

        override fun derive(value: Any) {
            queue.add(Registration(realizing, value))
        }

        override fun deriveFromSourceSet(name: String, action: Context.(KotlinSourceSet) -> Unit) {
            sourceSets.withSourceSet(name) { sourceSet, _ ->
                action(sourceSet)
                realize()
            }
        }
    }

    private class Registration(val parent: Registration?, val value: Any)
}