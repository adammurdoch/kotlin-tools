package net.rubygrapefruit.plugins.app.internal.tasks

import net.rubygrapefruit.plugins.app.Application
import net.rubygrapefruit.plugins.app.internal.DefaultDistribution
import net.rubygrapefruit.plugins.app.internal.HasLauncherScripts
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
        val defaultDist = app.distribution.orNull

        println("Application: ${app.appName.get()}")
        println()

        val distributions = app.distributions.get().filterIsInstance<DefaultDistribution>().sortedBy {
            when {
                it == defaultDist -> 1
                it.canBuildOnHostMachine -> 2
                else -> 3
            }
        }
        println("Distributions:")
        for (distribution in distributions) {
            println()
            print("Name: ${distribution.name}")
            if (!(distribution.canBuildOnHostMachine)) {
                print(" (not buildable)")
            }
            if (distribution == defaultDist) {
                print(" (DEFAULT)")
            }
            println()
            println("Launcher: ${launcherFor(distribution)}")
            println("Target machine: ${distribution.targetMachine}")
            println("Build type: ${distribution.buildType}")
            println("Dist task: ${distribution.distTask.name}")
            println("Image dir: ${distribution.imageDirectory.get()}")
            println("Launcher path: ${distribution.launcherFilePath.get()}")
        }
    }

    private fun launcherFor(distribution: DefaultDistribution): String {
        return when (distribution) {
            is HasLauncherScripts -> "launcher scripts"
            else -> "default"
        }
    }
}