package net.rubygrapefruit.plugins.app.internal.tasks

import net.rubygrapefruit.plugins.app.internal.DefaultDistribution
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import java.util.concurrent.Callable
import kotlin.io.path.relativeTo

abstract class Distributions : DefaultTask() {
    @get:Option(option = "all", description = "Builds distributions for all targets")
    @get:Internal
    abstract val all: Property<Boolean>

    @get:Internal
    abstract val defaultDistribution: Property<DefaultDistribution>

    @get:Internal
    abstract val allDistributions: SetProperty<DefaultDistribution>

    init {
        all.convention(false)
        dependsOn(object : Callable<Any> {
            override fun call(): Any {
                return if (all.get()) {
                    allDistributions.map { it.map { it.distTask } }
                } else {
                    defaultDistribution.map { it.distTask }
                }
            }
        })
    }

    @TaskAction
    fun report() {
        if (!all.get()) {
            println("Installed into ${defaultDistribution.get().imageOutputDirectory.get()}")
            val launcher = defaultDistribution.get().launcherOutputFile.get().asFile.toPath()
            println("Run using: $launcher")
        }
    }
}