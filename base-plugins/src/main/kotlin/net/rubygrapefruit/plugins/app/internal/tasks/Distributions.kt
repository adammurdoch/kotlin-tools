package net.rubygrapefruit.plugins.app.internal.tasks

import net.rubygrapefruit.plugins.app.Distribution
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import java.util.concurrent.Callable

abstract class Distributions : DefaultTask() {
    @get:Option(option = "all", description = "Builds distributions for all targets")
    @get:Internal
    abstract val all: Property<Boolean>

    @get:Internal
    abstract val defaultDistribution: Property<Distribution.Outputs>

    @get:Internal
    abstract val allDistributions: SetProperty<Distribution.Outputs>

    init {
        all.convention(false)
        dependsOn(object : Callable<Any> {
            override fun call(): Any {
                return if (all.get()) {
                    allDistributions.map { it.map { it.imageDirectory } }
                } else {
                    defaultDistribution.map { it.imageDirectory }
                }
            }
        })
    }

    @TaskAction
    fun report() {
        if (!all.get()) {
            val distribution = defaultDistribution.get()
            println("Installed into ${distribution.imageDirectory.get()}")
            val launcher = distribution.launcherFile.get().asFile.toPath()
            println("Run using: $launcher")
        }
    }
}