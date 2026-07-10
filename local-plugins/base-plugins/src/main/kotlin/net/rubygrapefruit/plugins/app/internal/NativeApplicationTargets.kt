package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.internal.component.MutableComponent
import org.gradle.api.model.ObjectFactory
import javax.inject.Inject

class NativeApplicationTargets @Inject constructor(
    private val objects: ObjectFactory
) {
    private var macOS: DefaultNativeComponent? = null

    fun visitTargets(consumer: (PlatformContribution) -> Unit) {
        if (macOS != null) {
            consumer(macOS!!)
        }
    }

    fun macOS(): DefaultNativeComponent {
        if (macOS == null) {
            val component = objects.newInstance(DefaultNativeComponent::class.java, "macosMain")
            macOS = component
        }
        return macOS!!
    }
}