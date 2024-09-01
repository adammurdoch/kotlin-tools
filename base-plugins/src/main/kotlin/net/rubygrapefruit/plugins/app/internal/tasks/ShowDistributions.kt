package net.rubygrapefruit.plugins.app.internal.tasks

import net.rubygrapefruit.plugins.app.Application
import net.rubygrapefruit.plugins.app.internal.DefaultDistribution
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

abstract class ShowDistributions : DefaultTask() {
    @get:Internal
    abstract val app: Property<Application>

    @TaskAction
    fun report() {
        val app = app.get()

        println("Application: ${app.appName.get()}")
        println()
        println("Distributions:")
        for (distribution in app.distributions.get()) {
            require(distribution is DefaultDistribution)
            println()
            print("Name: ${distribution.name}")
            if (distribution.isDefault) {
                print(" (DEFAULT)")
            }
            println()
            println("Dist task: ${distribution.distTask.name}")
            println("Image dir: ${distribution.imageDirectory.get()}")
            println("Launcher path: ${distribution.launcherFilePath.get()}")
        }
    }
}