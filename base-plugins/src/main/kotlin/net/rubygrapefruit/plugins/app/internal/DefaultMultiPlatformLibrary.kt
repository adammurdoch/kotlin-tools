package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.JvmLibrary
import net.rubygrapefruit.plugins.app.JvmModule
import net.rubygrapefruit.plugins.app.MultiPlatformLibrary
import net.rubygrapefruit.plugins.bootstrap.Versions
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler
import javax.inject.Inject

internal open class DefaultMultiPlatformLibrary @Inject constructor(
    private val componentRegistry: MultiPlatformComponentRegistry,
    private val factory: ObjectFactory,
    private val project: Project
) : MultiPlatformLibrary {
    private var jvm: JvmLibrary? = null

    val module: JvmModule
        get() = jvm!!.module

    override fun jvm() {
        jvm {}
    }

    override fun jvm(config: JvmLibrary.() -> Unit) {
        if (jvm == null) {
            val lib = factory.newInstance(JvmLibrary::class.java)
            lib.module.name.convention(toModuleName(project.name))
            lib.targetJavaVersion.convention(Versions.java)
            jvm = lib
            componentRegistry.jvm(lib.targetJavaVersion)
        }
        config(jvm!!)
    }

    override fun browser() {
        componentRegistry.browser()
    }

    override fun macOS() {
        componentRegistry.macOS()
    }

    override fun nativeDesktop() {
        componentRegistry.desktop()
    }

    override fun common(config: KotlinDependencyHandler.() -> Unit) {
        project.kotlin.sourceSets.getByName("commonMain").dependencies { config() }
    }

    override fun test(config: KotlinDependencyHandler.() -> Unit) {
        project.kotlin.sourceSets.getByName("commonTest").dependencies { config() }
    }
}