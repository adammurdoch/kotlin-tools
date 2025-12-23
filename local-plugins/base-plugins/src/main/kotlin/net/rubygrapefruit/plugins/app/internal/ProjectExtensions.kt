package net.rubygrapefruit.plugins.app.internal

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.TaskProvider
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

val Project.applications: ApplicationRegistry
    get() = extensions.getByType(ApplicationRegistry::class.java)

val Project.multiplatformComponents: MultiPlatformComponentRegistry
    get() = extensions.getByType(MultiPlatformComponentRegistry::class.java)

val Project.kotlin: KotlinMultiplatformExtension
    get() = extensions.getByType(KotlinMultiplatformExtension::class.java)

val Project.jvmKotlin: KotlinJvmProjectExtension
    get() = extensions.getByType(KotlinJvmProjectExtension::class.java)

inline fun <reified T : DefaultTask> TaskContainer.registering(name: String, crossinline action: T.() -> Unit): TaskProvider<T> {
    return register(name, T::class.java) { task -> action(task) }
}

internal fun Project.settingsPluginApplied() {
    extensions.add("__settings_plugin_applied__", "true")
}

internal fun Project.checkSettingsPluginApplied() {
    if (rootProject.extensions.findByName("__settings_plugin_applied__") == null) {
        throw IllegalStateException("Plugin 'net.rubygrapefruit.kotlin-base' must be applied to the settings of this build.")
    }
}
