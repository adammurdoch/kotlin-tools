package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.Dependencies
import net.rubygrapefruit.plugins.app.internal.component.MutableComponent
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import javax.inject.Inject

class NativeApplicationTargets @Inject constructor(
    private val componentRegistry: MultiPlatformComponentRegistry,
    private val objects: ObjectFactory,
    private val project: Project
) {
    private var macOS: DefaultNativeComponent? = null

    val mainSourceSet get() = project.kotlin.sourceSets.getByName("commonMain")

    fun visitTargets(consumer: (MutableComponent) -> Unit) {
        if (macOS != null) {
            consumer(macOS!!)
        }
    }

    fun common(config: Dependencies.() -> Unit) {
        mainSourceSet.dependencies { config(KotlinHandlerBackedDependencies(this)) }
    }

    fun test(config: Dependencies.() -> Unit) {
        project.kotlin.sourceSets.getByName("commonTest").dependencies { config(KotlinHandlerBackedDependencies(this)) }
    }

    fun macOS(): DefaultNativeComponent {
        if (macOS == null) {
            val component = objects.newInstance(DefaultNativeComponent::class.java, "macosMain")
            macOS = component
        }
        return macOS!!
    }
}