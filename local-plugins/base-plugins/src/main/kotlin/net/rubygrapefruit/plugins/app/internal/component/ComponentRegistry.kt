package net.rubygrapefruit.plugins.app.internal.component

import net.rubygrapefruit.plugins.app.internal.SourceSets
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import kotlin.reflect.KClass
import kotlin.reflect.cast

open class ComponentRegistry(private val project: Project) {
    private val rules = RuleSet()
    private val context = DefaultContext(SourceSets(project)) { value, context ->
        applyRules(Phase.Prepare, value, context)
        applyRules(Phase.Derive, value, context)
    }
    val factory: ComponentFactory = DefaultComponentFactory { value ->
        applyRules(Phase.Initialize, Registration(null, value), context)
    }
    private var main: MutableComponent? = null

    fun register(component: MutableComponent) {
        if (main != null) {
            throw UnsupportedOperationException("Support for multiple root components in the same project is not implemented.")
        }
        main = component
        val registration = Registration(null, component)
        applyRules(Phase.Initialize, registration, context)
        project.afterEvaluate {
            context.start(registration)
//            applyActions.clear()
//            deriveActions.clear()
        }
    }

    fun <T : Any> each(type: KClass<T>, action: Rules<T>.() -> Unit) {
        Rules(type, rules).action()
    }

    inline fun <reified T : Any> each(noinline action: Rules<T>.() -> Unit) {
        each(T::class, action)
    }

    private fun applyRules(phase: Phase, registration: Registration, context: Context) {
        for (rule in rules.rules) {
            rule.maybeApply(phase, registration.value, registration.parent?.value, context)
        }
    }

    internal enum class Phase {
        /**
         * Initialize target before user configuration. Runs when component is created.
         */
        Initialize,

        /**
         * Mutates target. Runs after user configuration phase, prior to derive.
         */
        Prepare,

        /**
         * Derives objects from target. Runs after prepare
         */
        Derive
    }

    internal sealed class Rule {
        abstract fun maybeApply(phase: Phase, component: Any, parent: Any?, context: Context)
    }

    private class ApplyToType<T : Any>(
        val phase: Phase,
        val type: KClass<T>,
        val action: Context.(T) -> Unit
    ) : Rule() {
        override fun maybeApply(phase: Phase, component: Any, parent: Any?, context: Context) {
            if (phase == this.phase && type.isInstance(component)) {
                context.action(type.cast(component))
            }
        }
    }

    private class ApplyToTypeWithParent<T : Any, P : Any>(
        val phase: Phase,
        val type: KClass<T>,
        val parentType: KClass<P>,
        val action: Context.(T, P) -> Unit
    ) : Rule() {
        override fun maybeApply(phase: Phase, component: Any, parent: Any?, context: Context) {
            if (phase == this.phase && parentType.isInstance(parent) && type.isInstance(component)) {
                context.action(type.cast(component), parentType.cast(parent))
            }
        }
    }

    private class DeriveFromTypeWithParentAndDep<T : Any, P : Any, D : Any>(
        val type: KClass<T>,
        val parentType: KClass<P>,
        val depType: KClass<D>,
        val action: Context.(T, P, D) -> Unit
    ) : Rule() {
        private val deps = mutableMapOf<P, D>()
        private val waiting = mutableMapOf<P, MutableList<T>>()

        override fun maybeApply(phase: Phase, component: Any, parent: Any?, context: Context) {
            if (phase != Phase.Derive) {
                return
            }
            if (parentType.isInstance(parent)) {
                val typedParent = parentType.cast(parent)
                if (type.isInstance(component)) {
                    val dep = deps[parent]
                    val typedComponent = type.cast(component)
                    if (dep != null) {
                        context.action(typedComponent, typedParent, dep)
                    } else {
                        waiting.getOrPut(typedParent) { mutableListOf() }.add(typedComponent)
                    }
                }
                if (depType.isInstance(component)) {
                    val dep = depType.cast(component)
                    deps[typedParent] = dep

                    waiting.remove(typedParent)?.forEach {
                        context.action(it, typedParent, dep)
                    }
                }
            }
        }
    }

    internal class RuleSet {
        val rules = mutableListOf<Rule>()

        fun add(rule: Rule) {
            rules.add(rule)
        }
    }

    class Rules<T : Any> internal constructor(
        private val type: KClass<T>,
        private val rules: RuleSet
    ) {
        /**
         * Initializes the target when it is created. Only applied to objects create via [ComponentFactory].
         */
        fun initialize(action: (T) -> Unit) {
            rules.add(ApplyToType(Phase.Initialize, type) { action(it) })
        }

        /**
         * Mutates the target. All prepare rules are run before any derive rules.
         */
        fun prepare(action: (T) -> Unit) {
            rules.add(ApplyToType(Phase.Prepare, type) { action(it) })
        }

        /**
         * Called after each value of T has been realized.
         */
        fun derive(action: Context.(T) -> Unit) {
            rules.add(ApplyToType(Phase.Derive, type, action))
        }

        fun <S : Any> each(childType: KClass<S>, action: Rules2<S, T>.() -> Unit) {
            Rules2(childType, type, rules).action()
        }

        inline fun <reified S : Any> each(noinline action: Rules2<S, T>.() -> Unit) {
            each(S::class, action)
        }
    }

    class Rules2<S : Any, T : Any> internal constructor(
        private val type: KClass<S>,
        private val parentType: KClass<T>,
        private val rules: RuleSet
    ) {
        fun prepare(action: (S, T) -> Unit) {
            rules.add(ApplyToTypeWithParent(Phase.Prepare, type, parentType) { value, parent -> action(value, parent) })
        }

        /**
         * Called after each value has been realized.
         */
        fun derive(action: Context.(S, T) -> Unit) {
            rules.add(ApplyToTypeWithParent(Phase.Derive, type, parentType, action))
        }

        fun <U : Any> require(depType: KClass<U>, action: Rules3<S, T, U>.() -> Unit) {
            Rules3(type, parentType, depType, rules).action()
        }

        inline fun <reified U : Any> require(noinline action: Rules3<S, T, U>.() -> Unit) {
            require(U::class, action)
        }
    }

    class Rules3<S : Any, T : Any, U : Any> internal constructor(
        private val type: KClass<S>,
        private val parentType: KClass<T>,
        private val depType: KClass<U>,
        private val rules: RuleSet
    ) {
        /**
         * Called after each value has been realized.
         */
        fun derive(action: Context.(S, T, U) -> Unit) {
            rules.add(DeriveFromTypeWithParentAndDep(type, parentType, depType, action))
        }
    }

    interface Context {
        /**
         * Registers a child of the current object.
         */
        fun register(value: Any)

        fun registerSibling(value: Any)

        /**
         * Runs the given action when the given source set is available.
         * This is used because the Kotlin plugin creates some source sets lazily.
         */
        fun deriveFromSourceSet(name: String, action: Context.(KotlinSourceSet) -> Unit)
    }

    private class DefaultContext(val sourceSets: SourceSets, val realizeAction: (Registration, Context) -> Unit) : Context {
        private val queue = mutableListOf<Registration>()
        private var realizing: Registration? = null

        fun start(registration: Registration) {
            queue.add(registration)
            realize()
        }

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
            require(realizing != null)
            queue.add(Registration(realizing, value))
        }

        override fun registerSibling(value: Any) {
            require(realizing != null)
            queue.add(Registration(realizing?.parent, value))
        }

        override fun deriveFromSourceSet(name: String, action: Context.(KotlinSourceSet) -> Unit) {
            sourceSets.withSourceSet(name) { sourceSet, _ ->
                action(sourceSet)
                realize()
            }
        }
    }

    private class DefaultComponentFactory(private val consumer: (Any) -> Unit) : ComponentFactory {
        override fun created(component: Any) {
            consumer(component)
        }
    }

    private class Registration(val parent: Registration?, val value: Any)
}