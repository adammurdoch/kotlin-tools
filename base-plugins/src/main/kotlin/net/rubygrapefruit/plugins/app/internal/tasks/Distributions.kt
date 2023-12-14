package net.rubygrapefruit.plugins.app.internal.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.Task
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.options.Option
import java.util.concurrent.Callable

abstract class Distributions : DefaultTask() {
    @get:Option(option = "all", description = "Builds distributions for all targets")
    @get:Internal
    abstract val all: Property<Boolean>

    @get:Internal
    abstract val defaultDist: Property<Task>

    @get:Internal
    abstract val allDists: SetProperty<Task>

    init {
        all.convention(false)
        dependsOn(object : Callable<Any> {
            override fun call(): Any {
                return if (all.get()) {
                    allDists
                } else {
                    defaultDist
                }
            }
        })
    }
}