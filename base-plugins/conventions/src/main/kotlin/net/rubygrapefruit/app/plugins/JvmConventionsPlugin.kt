package net.rubygrapefruit.app.plugins

import net.rubygrapefruit.app.internal.JvmModuleRegistry
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.jvm.toolchain.JavaLanguageVersion

class JvmConventionsPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins.withId("java") {
                val java = extensions.getByType(JavaPluginExtension::class.java)
                java.toolchain.languageVersion.set(JavaLanguageVersion.of(11))
            }
            extensions.create("jvmModules", JvmModuleRegistry::class.java)
        }
    }
}