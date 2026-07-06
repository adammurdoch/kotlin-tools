package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.*
import net.rubygrapefruit.plugins.app.internal.component.MutableComponent
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import javax.inject.Inject

internal open class DefaultMultiPlatformLibrary @Inject constructor(
    private val componentRegistry: MultiPlatformComponentRegistry,
    private val factory: ObjectFactory,
    private val project: Project
) : MultiPlatformLibrary, MutableComponent, HasTargets {
    private var jvm: DefaultJvmLibrary? = null
    private var macOs: DefaultNativeLibrary? = null

    val module: JvmModule
        get() = createJvm().module

    override fun visitTargets(consumer: (MutableComponent) -> Unit) {
        if (jvm != null) {
            consumer(jvm!!)
        }
        if (macOs != null) {
            consumer(macOs!!)
        }
    }

    override fun jvm() {
        jvm {}
    }

    override fun jvm(config: JvmLibrary.() -> Unit) {
        config(createJvm())
    }

    override fun browser() {
        componentRegistry.browser()
    }

    override fun macOS() {
        componentRegistry.macOS()
    }

    override fun macOS(config: NativeLibrary.() -> Unit) {
        componentRegistry.macOS()
        config(createMacOS())
    }

    override fun desktop(config: NativeLibrary.() -> Unit) {
        val lib = factory.newInstance(DefaultNativeLibrary::class.java, "desktopMain")
        config(lib)
    }

    override fun nativeDesktop() {
        componentRegistry.nativeDesktop()
    }

    override fun common(config: LibraryDependencies.() -> Unit) {
        project.kotlin.sourceSets.getByName("commonMain").dependencies { config(KotlinHandlerBackedLibraryDependencies(this)) }
    }

    override fun test(config: LibraryDependencies.() -> Unit) {
        project.kotlin.sourceSets.getByName("commonTest").dependencies { config(KotlinHandlerBackedLibraryDependencies(this)) }
    }

    private fun createJvm(): JvmLibrary {
        if (jvm == null) {
            val lib = factory.newInstance(DefaultJvmLibrary::class.java, "jvmMain", "jvmTest")
            lib.module.name.convention(toModuleName(project.name))
            lib.targetJvmVersion.convention(Versions.libs.jvm.version)
            jvm = lib
            // This can call back to query JVM object
            componentRegistry.jvm(lib.targetJvmVersion)
        }
        return jvm!!
    }

    private fun createMacOS(): DefaultNativeLibrary {
        if (macOs == null) {
            macOs = factory.newInstance(DefaultNativeLibrary::class.java, "macosMain")
        }
        return macOs!!
    }
}