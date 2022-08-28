package net.rubygrapefruit.app.plugins

import net.rubygrapefruit.app.internal.DefaultJvmCliApplication
import net.rubygrapefruit.app.internal.applications
import net.rubygrapefruit.app.tasks.LauncherScript
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.jvm.tasks.Jar

class JvmCliApplicationPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins.apply("java-library")
            plugins.apply("org.jetbrains.kotlin.jvm")
            plugins.apply(ApplicationBasePlugin::class.java)

            val app = extensions.create("application", DefaultJvmCliApplication::class.java)
            app.setup()
            applications.register(app)

            val jarTask = tasks.named("jar", Jar::class.java)
            val runtimeClasspath = configurations.getByName("runtimeClasspath")

            app.distribution.libraries.from(jarTask.map { it.archiveFile })
            app.distribution.libraries.from(runtimeClasspath)

            val launcherTask = tasks.register("launcherScript", LauncherScript::class.java) {
                it.scriptFile.set(layout.buildDirectory.file("app/launcher.sh"))
                it.module.set(app.module)
                it.mainClass.set(app.mainClass)
                it.modulePath.add(jarTask.flatMap { it.archiveFileName })
                val libNames = runtimeClasspath.elements.map { it.map { f -> f.asFile.name } }
                it.modulePath.addAll(libNames)
            }
            app.distribution.launcherFile.set(launcherTask.flatMap { it.scriptFile })
        }
    }
}