package net.rubygrapefruit.app.internal

import net.rubygrapefruit.app.CliApplication
import net.rubygrapefruit.app.tasks.DistributionImage
import org.gradle.api.Project

internal abstract class ApplicationRegistry(private val project: Project) {
    private var main: CliApplication? = null

    fun register(app: CliApplication) {
        if (main != null) {
            throw UnsupportedOperationException("Support for multiple applications in the same project is not implemented.")
        }
        main = app

        app.distribution.get().imageDirectory.set(project.layout.buildDirectory.dir("dist-image"))

        project.tasks.register("dist", DistributionImage::class.java) { t ->
            t.imageDirectory.set(app.distribution.flatMap { d -> d.imageDirectory })
            t.launcherFile.set(app.distribution.flatMap { d -> d.launcherFile })
            t.launcherBaseName.set(project.name)
        }
    }
}