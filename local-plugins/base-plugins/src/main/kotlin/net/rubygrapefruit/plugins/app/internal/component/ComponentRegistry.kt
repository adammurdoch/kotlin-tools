package net.rubygrapefruit.plugins.app.internal.component

import net.rubygrapefruit.plugins.app.internal.SourceSets
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import kotlin.reflect.KClass
import kotlin.reflect.cast

open class ComponentRegistry(private val project: Project) {
    private val rules = RuleSet()
    private val context = DefaultContext(SourceSets(project), ::applyRules)
    private var main: Registration? = null
    val factory: ComponentFactory = DefaultComponentFactory { value ->
        require(main != null)
        context.start(Registration(main, value))
    }

    fun register(component: MutableComponent) {
        if (main != null) {
            throw UnsupportedOperationException("Support for multiple root components in the same project is not implemented.")
        }
        val registration = Registration(null, component)
        main = registration
        context.start(registration)
        project.afterEvaluate {
            context.realizeAll()
//            applyActions.clear()
//            deriveActions.clear()
        }
    }

    fun <T : Any> each(type: KClass<T>, action: Rules<T>.() -> Unit) {
        AllWithTypeRules(type, rules).action()
    }

    inline fun <reified T : Any> each(noinline action: Rules<T>.() -> Unit) {
        each(T::class, action)
    }

    private fun applyRules(phase: Phase, registration: Registration, context: Context) {
        for (rule in rules.rules) {
            rule.maybeApply(phase, registration, context)
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

    private sealed class Rule {
        abstract fun maybeApply(phase: Phase, registration: Registration, context: Context)
    }

    private class ApplyToType<T : Any>(
        val phase: Phase,
        val type: KClass<T>,
        val action: Context.(T) -> Unit
    ) : Rule() {
        override fun maybeApply(phase: Phase, registration: Registration, context: Context) {
            val component = registration.value
            if (phase == this.phase && type.isInstance(component)) {
                context.action(type.cast(component))
            }
        }
    }

    private class ApplyToTypeWithAncestor<T : Any, P : Any>(
        val phase: Phase,
        val type: KClass<T>,
        val parentType: KClass<P>,
        val action: Context.(T, P) -> Unit
    ) : Rule() {
        override fun maybeApply(phase: Phase, registration: Registration, context: Context) {
            val component = registration.value
            val parent = registration.findAncestor(parentType)
            if (phase == this.phase && parent != null && type.isInstance(component)) {
                context.action(type.cast(component), parent)
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

        override fun maybeApply(phase: Phase, registration: Registration, context: Context) {
            if (phase != Phase.Derive) {
                return
            }
            val component = registration.value
            val parent = registration.parent?.value
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

    private class RuleSet {
        val rules = mutableListOf<Rule>()

        fun add(rule: Rule) {
            rules.add(rule)
        }
    }

    abstract class Rules<T : Any> {
        /**
         * Initializes the target during project configuration. Applied to objects create via [ComponentFactory] or registered explicitly.
         * This is intended to be used when some plugin requires configuration to happen before project's afterEvaluate.
         *
         * Should prefer using [prepare].
         */
        abstract fun initialize(action: Context.(T) -> Unit)

        /**
         * Mutates the target. All prepare rules are run before any derive rules.
         */
        abstract fun prepare(action: (T) -> Unit)

        /**
         * Called after each value of T has been realized.
         */
        abstract fun derive(action: Context.(T) -> Unit)

        /**
         * Applies rules to each descendent of the given type.
         */
        abstract fun <S : Any> each(descendentType: KClass<S>, action: Rules2<S, T>.() -> Unit)

        /**
         * Applies rules to each descendent of the given type.
         */
        inline fun <reified S : Any> each(noinline action: Rules2<S, T>.() -> Unit) {
            each(S::class, action)
        }
    }

    private class AllWithTypeRules<T : Any>(
        private val type: KClass<T>,
        private val rules: RuleSet
    ) : Rules<T>() {
        override fun initialize(action: Context.(T) -> Unit) {
            rules.add(ApplyToType(Phase.Initialize, type) { action(it) })
        }

        override fun prepare(action: (T) -> Unit) {
            rules.add(ApplyToType(Phase.Prepare, type) { action(it) })
        }

        override fun derive(action: Context.(T) -> Unit) {
            rules.add(ApplyToType(Phase.Derive, type, action))
        }

        override fun <S : Any> each(descendentType: KClass<S>, action: Rules2<S, T>.() -> Unit) {
            AllWithTypeAndAncestor(descendentType, type, rules).action()
        }
    }

    abstract class Rules2<S : Any, T : Any> {
        abstract fun prepare(action: (S, T) -> Unit)

        /**
         * Called after each value has been realized.
         */
        abstract fun derive(action: Context.(S, T) -> Unit)

        /**
         * Requires a sibling with the given type.
         */
        abstract fun <U : Any> require(depType: KClass<U>, action: Rules3<S, T, U>.() -> Unit)

        inline fun <reified U : Any> require(noinline action: Rules3<S, T, U>.() -> Unit) {
            require(U::class, action)
        }
    }

    private class AllWithTypeAndAncestor<S : Any, T : Any>(
        private val type: KClass<S>,
        private val parentType: KClass<T>,
        private val rules: RuleSet
    ) : Rules2<S, T>() {
        override fun prepare(action: (S, T) -> Unit) {
            rules.add(ApplyToTypeWithAncestor(Phase.Prepare, type, parentType) { value, parent -> action(value, parent) })
        }

        override fun derive(action: Context.(S, T) -> Unit) {
            rules.add(ApplyToTypeWithAncestor(Phase.Derive, type, parentType, action))
        }

        override fun <U : Any> require(depType: KClass<U>, action: Rules3<S, T, U>.() -> Unit) {
            DefaultRules3(type, parentType, depType, rules).action()
        }

    }

    abstract class Rules3<S : Any, T : Any, U : Any> {
        /**
         * Called after each value has been realized.
         */
        abstract fun derive(action: Context.(S, T, U) -> Unit)
    }

    private class DefaultRules3<S : Any, T : Any, U : Any>(
        private val type: KClass<S>,
        private val parentType: KClass<T>,
        private val depType: KClass<U>,
        private val rules: RuleSet
    ) : Rules3<S, T, U>() {
        override fun derive(action: Context.(S, T, U) -> Unit) {
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

    private class DefaultContext(val sourceSets: SourceSets, val applyRules: (Phase, Registration, Context) -> Unit) : Context {
        private val all = mutableSetOf<Any>()
        private val initialized = mutableListOf<Registration>()
        private val queue = mutableListOf<Registration>()
        private var initializing = true
        private var visiting: Registration? = null

        fun start(registration: Registration) {
            register(registration)
            applyRulesToQueued()
        }

        fun realizeAll() {
            require(queue.isEmpty() && visiting == null)
            initializing = false
            queue.addAll(initialized)
            initialized.clear()
            applyRulesToQueued()
        }

        private fun applyRulesToQueued() {
            if (visiting != null) {
                // Already working on the queue
                return
            }
            while (queue.isNotEmpty()) {
                val registration = queue.removeFirst()
                visiting = registration
                if (initializing) {
                    applyRules(Phase.Initialize, registration, this)
                    initialized.add(registration)
                } else {
                    applyRules(Phase.Prepare, registration, this)
                    applyRules(Phase.Derive, registration, this)
                }
            }
            visiting = null
        }

        override fun register(value: Any) {
            require(visiting != null)
            register(Registration(visiting, value))
        }

        override fun registerSibling(value: Any) {
            require(visiting != null)
            register(Registration(visiting?.parent, value))
        }

        private fun register(registration: Registration) {
            if (!all.add(registration.value)) {
                throw IllegalStateException("Object ${registration.value} already registered")
            }
            queue.add(registration)
        }

        override fun deriveFromSourceSet(name: String, action: Context.(KotlinSourceSet) -> Unit) {
            sourceSets.withSourceSet(name) { sourceSet, _ ->
                action(sourceSet)
                applyRulesToQueued()
            }
        }
    }

    private class DefaultComponentFactory(private val consumer: (Any) -> Unit) : ComponentFactory {
        override fun <T : Any> created(component: T): T {
            consumer(component)
            return component
        }
    }

    private class Registration(val parent: Registration?, val value: Any) {
        override fun toString(): String {
            return if (parent != null) {
                "$parent > $value"
            } else {
                value.toString()
            }
        }

        fun <T : Any> findAncestor(parentType: KClass<T>): T? {
            return parent?.find(parentType)
        }

        private fun <T : Any> find(type: KClass<T>): T? {
            return if (type.isInstance(value)) {
                type.cast(value)
            } else {
                parent?.find(type)
            }
        }
    }
}