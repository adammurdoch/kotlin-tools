package net.rubygrapefruit.plugins.app.internal.tasks

import net.rubygrapefruit.plugins.app.internal.BuildableDistribution
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
    abstract val defaultDistribution: Property<BuildableDistribution>

    @get:Internal
    abstract val allDistributions: SetProperty<BuildableDistribution>

    init {
        all.convention(false)
        dependsOn(object : Callable<Any> {
            override fun call(): Any {
                return if (all.get()) {
                    allDistributions.map { it.map { it.distProducer } }
                } else {
                    defaultDistribution.map { it.distProducer }
                }
            }
        })
    }

    @TaskAction
    fun report() {
        if (!all.get()) {
            println("Installed into ${defaultDistribution.get().outputs.imageDirectory.get()}")
            val launcher = defaultDistribution.get().outputs.launcherFile.get().asFile.toPath()
            println("Run using: $launcher")
        }
    }
}