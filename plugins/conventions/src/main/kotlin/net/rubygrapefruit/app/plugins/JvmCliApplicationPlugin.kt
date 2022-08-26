package net.rubygrapefruit.app.plugins

import net.rubygrapefruit.app.internal.ApplicationRegistry
import net.rubygrapefruit.app.internal.DefaultJvmCliApplication
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.jvm.tasks.Jar

class JvmCliApplicationPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins.apply("org.jetbrains.kotlin.jvm")
            plugins.apply(ApplicationBasePlugin::class.java)

            val app = extensions.create("application", DefaultJvmCliApplication::class.java)
            app.setup()
            extensions.getByType(ApplicationRegistry::class.java).register(app)
            app.distribution.get().libraries.from(tasks.named("jar", Jar::class.java).map { it.archiveFile })
        }
    }
}