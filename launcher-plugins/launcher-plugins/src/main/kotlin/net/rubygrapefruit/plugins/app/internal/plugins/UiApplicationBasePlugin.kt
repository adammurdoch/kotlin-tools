package net.rubygrapefruit.plugins.app.internal.plugins

import net.rubygrapefruit.plugins.app.internal.DefaultUiApplication
import net.rubygrapefruit.plugins.app.internal.tasks.AppIcon
import net.rubygrapefruit.plugins.app.internal.tasks.InfoPlist
import net.rubygrapefruit.plugins.app.internal.tasks.ReleaseDistribution
import net.rubygrapefruit.plugins.app.internal.applications
import net.rubygrapefruit.plugins.app.internal.tasks.DistributionImage
import org.gradle.api.Plugin
import org.gradle.api.Project

class UiApplicationBasePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins.apply(ApplicationBasePlugin::class.java)
            applications.withApp<DefaultUiApplication> { app ->
                val capitalizedAppName = app.capitalizedAppName
                app.iconFile.set(layout.projectDirectory.file("src/main/Icon1024.png"))

                val infoPlistTask = tasks.register("infoPlist", InfoPlist::class.java) {
                    it.plistFile.set(layout.buildDirectory.file("app/Info.plist"))
                    it.bundleName.set(capitalizedAppName)
                    it.bundleIdentifier.set(capitalizedAppName)
                    it.executableName.set(capitalizedAppName)
                    it.iconName.set(app.iconName)
                }

                val iconTask = tasks.register("appIcon", AppIcon::class.java) {
                    it.outputIconSet.set(layout.buildDirectory.dir("app/app.iconset"))
                    it.outputIcon.set(layout.buildDirectory.file("app/app.icns"))
                    it.sourceIcon.set(app.iconFile)
                }

                app.distribution.launcherFilePath.set(capitalizedAppName.map { "MacOS/$it" })

                applications.applyToDistribution { dist ->
                    dist.imageDirectory.set(layout.buildDirectory.dir(capitalizedAppName.map { "debug/$it.app" }))
                    dist.rootDirPath.set("Contents")
                    dist.includeFile("Info.plist", infoPlistTask.flatMap { it.plistFile })
                    dist.includeFile(
                        app.iconName.map { "Resources/$it" },
                        iconTask.flatMap {
                            if (it.sourceIcon.get().asFile.exists()) {
                                it.outputIcon
                            } else {
                                // Should be able to return 'null' here to mean "there is no icon". This works with the
                                // Gradle APIs, but is broken for Kotlin compilation
                                val dummyFile = layout.buildDirectory.file(DistributionImage.FileContribution.dummyName)
                                with(dummyFile.get().asFile) {
                                    parentFile.mkdirs()
                                    createNewFile()
                                }
                                dummyFile
                            }
                        })
                }

                tasks.register("releaseDist", ReleaseDistribution::class.java) { t ->
                    t.imageDirectory.set(layout.buildDirectory.dir(capitalizedAppName.map { "release/$it.app" }))
                    t.unsignedImage.set(app.distribution.imageOutputDirectory)
                    t.signingIdentity.set(app.signingIdentity)
                    t.notarizationProfileName.set(app.notarizationProfileName)
                }
            }
        }
    }
}