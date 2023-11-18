package net.rubygrapefruit.plugins.app.internal.plugins

import net.rubygrapefruit.plugins.app.JvmLibrary
import net.rubygrapefruit.plugins.app.internal.DefaultJvmLibrary
import net.rubygrapefruit.plugins.app.internal.JvmModuleRegistry
import net.rubygrapefruit.plugins.app.internal.toModuleName
import net.rubygrapefruit.plugins.bootstrap.Versions
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSetContainer
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

class JvmLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins.apply("java-library")
            plugins.apply("org.jetbrains.kotlin.jvm")
            plugins.apply(LibraryBasePlugin::class.java)
            plugins.apply(JvmConventionsPlugin::class.java)

            val lib = extensions.create(JvmLibrary::class.java, "library", DefaultJvmLibrary::class.java)
            lib.module.name.convention(toModuleName(project.name))
            lib.targetJavaVersion.convention(Versions.java)

            JvmConventionsPlugin.javaVersion(this, lib.targetJavaVersion)

            val runtimeClasspath = configurations.getByName("runtimeClasspath")
            val apiConfig = configurations.getByName("api")

            val apiClasspath = configurations.create("apiClasspath")
            apiClasspath.extendsFrom(apiConfig)

            val classesDir = files(tasks.named("compileKotlin", KotlinCompile::class.java).map { it.destinationDirectory })

            val moduleInfoCp = extensions.getByType(JvmModuleRegistry::class.java).inspectClassPathsFor(lib.module, null, classesDir, apiClasspath, runtimeClasspath).moduleInfoClasspath

            val sourceSet = extensions.getByType(SourceSetContainer::class.java).getByName("main")
            sourceSet.output.dir(moduleInfoCp)
        }
    }
}