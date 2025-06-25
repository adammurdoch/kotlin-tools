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

    @get:Option(option = "release", description = "Builds release distribution")
    @get:Internal
    abstract val release: Property<Boolean>

    @get:Internal
    abstract val devDistribution: Property<Distribution.Outputs>

    @get:Internal
    abstract val releaseDistribution: Property<Distribution.Outputs>

    @get:Internal
    abstract val allDistributions: SetProperty<Distribution.Outputs>

    init {
        all.convention(false)
        release.convention(false)
        dependsOn(object : Callable<Any> {
            override fun call(): Any {
                return if (all.get()) {
                    allDistributions.map { it.map { it.imageDirectory } }
                } else if (release.get()) {
                    releaseDistribution.map { it.imageDirectory }
                } else {
                    devDistribution.map { it.imageDirectory }
                }
            }
        })
    }

    @TaskAction
    fun report() {
        if (!all.get()) {
            val distribution = if (release.get()) releaseDistribution.get() else devDistribution.get()
            println("Installed into ${distribution.imageDirectory.get()}")
            val launcher = distribution.launcherFile.get().asFile.toPath()
            println("Run using: $launcher")
        }
    }
}