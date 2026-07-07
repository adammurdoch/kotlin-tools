package net.rubygrapefruit.plugins.app.internal.component

import net.rubygrapefruit.plugins.app.internal.SourceSets
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import kotlin.reflect.KClass
import kotlin.reflect.cast

open class ComponentRegistry(private val project: Project) {
    private val rules = RuleSet()
    private var main: MutableComponent? = null

    fun register(component: MutableComponent) {
        if (main != null) {
            throw UnsupportedOperationException("Support for multiple root components in the same project is not implemented.")
        }
        main = component
        project.afterEvaluate {
            val sourceSets = SourceSets(project)
            val context = DefaultContext(sourceSets) { value, context -> realize(value, context) }
            context.register(component)
            context.realize()
//            applyActions.clear()
//            deriveActions.clear()
        }
    }

    fun <T : Any> from(type: KClass<T>, action: Rules<T>.() -> Unit) {
        Rules(type, rules).action()
    }

    inline fun <reified T : Any> from(noinline action: Rules<T>.() -> Unit) {
        from(T::class, action)
    }

    private fun realize(registration: Registration, context: Context) {
        for (rule in rules.rules) {
            rule.maybeApply(registration.value, registration.parent?.value, context)
        }
    }

    internal sealed class Rule {
        abstract fun maybeApply(component: Any, parent: Any?, context: Context)
    }

    private class DeriveFromType<T : Any>(val type: KClass<T>, val action: Context.(T) -> Unit) : Rule() {
        override fun maybeApply(component: Any, parent: Any?, context: Context) {
            if (type.isInstance(component)) {
                context.action(type.cast(component))
            }
        }
    }

    private class DeriveFromTypeWithParent<T : Any, P : Any>(val type: KClass<T>, val parentType: KClass<P>, val action: Context.(T, P) -> Unit) : Rule() {
        override fun maybeApply(component: Any, parent: Any?, context: Context) {
            if (parentType.isInstance(parent) && type.isInstance(component)) {
                context.action(type.cast(component), parentType.cast(parent))
            }
        }
    }

    internal class RuleSet {
        val rules = mutableListOf<Rule>()

        fun add(rule: Rule) {
            rules.add(rule)
        }
    }

    class Rules<T : Any> internal constructor(private val type: KClass<T>, private val rules: RuleSet) {
        /**
         * Called after each value of T has been realized.
         */
        fun derive(action: Context.(T) -> Unit) {
            rules.add(DeriveFromType(type, action))
        }

        fun <S : Any> from(childType: KClass<S>, action: Rules2<S, T>.() -> Unit) {
            Rules2(childType, type, rules).action()
        }

        inline fun <reified S : Any> from(noinline action: Rules2<S, T>.() -> Unit) {
            from(S::class, action)
        }
    }

    class Rules2<S : Any, T : Any> internal constructor(private val type: KClass<S>, private val parentType: KClass<T>, private val rules: RuleSet) {
        /**
         * Called after each value has been realized.
         */
        fun derive(action: Context.(S, T) -> Unit) {
            rules.add(DeriveFromTypeWithParent(type, parentType, action))
        }

        fun <U : Any> require(depType: KClass<U>, action: Rules3<S, T, U>.() -> Unit) {
            TODO()
        }

        inline fun <reified U : Any> require(noinline action: Rules3<S, T, U>.() -> Unit) {
            require(U::class, action)
        }
    }

    class Rules3<S : Any, T : Any, U : Any> {
        /**
         * Called after each value has been realized.
         */
        fun derive(action: Context.(S, T, U) -> Unit) {
            TODO()
        }
    }

    interface Context {
        fun register(value: Any)

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

        override fun register(value: Any) {
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