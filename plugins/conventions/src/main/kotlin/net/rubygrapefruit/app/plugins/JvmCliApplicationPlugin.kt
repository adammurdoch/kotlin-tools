package net.rubygrapefruit.app.plugins

import net.rubygrapefruit.app.internal.ApplicationRegistry
import net.rubygrapefruit.app.internal.DefaultJvmCliApplication
import net.rubygrapefruit.app.tasks.LauncherScript
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

            val jarTask = tasks.named("jar", Jar::class.java)
            val runtimeClasspath = configurations.getByName("runtimeClasspath")

            app.distribution.get().libraries.from(jarTask.map { it.archiveFile })
            app.distribution.get().libraries.from(runtimeClasspath)

            val launcherTask = tasks.register("launcherScript", LauncherScript::class.java) {
                it.scriptFile.set(layout.buildDirectory.file("app/launcher.sh"))
                it.mainClass.set(app.mainClass)
                it.classPath.add(jarTask.flatMap { it.archiveFileName })
                val libNames = runtimeClasspath.elements.map { it.map { f -> f.asFile.name } }
                it.classPath.addAll(libNames)
            }
            app.distribution.get().launcherFile.set(launcherTask.flatMap { it.scriptFile })
        }
    }
}