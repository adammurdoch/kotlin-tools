package net.rubygrapefruit.plugins.app.internal.plugins

import net.rubygrapefruit.plugins.app.BuildType
import net.rubygrapefruit.plugins.app.internal.*
import net.rubygrapefruit.plugins.app.internal.tasks.AppIcon
import net.rubygrapefruit.plugins.app.internal.tasks.InfoPlist
import net.rubygrapefruit.plugins.app.internal.tasks.ReleaseDistribution
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider

class UiApplicationBasePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins.apply(ApplicationBasePlugin::class.java)

            componentRegistry.each<DefaultUiApplication> {
                derive { app ->
                    val infoPlistTask = tasks.register("infoPlist", InfoPlist::class.java) {
                        it.plistFile.set(layout.buildDirectory.file("app/Info.plist"))
                        it.bundleName.set(app.capitalizedAppName)
                        it.bundleIdentifier.set(app.capitalizedAppName)
                        it.executableName.set(app.capitalizedAppName)
                        it.iconName.set(app.iconName)
                    }

                    val iconTask = tasks.register("appIcon", AppIcon::class.java) {
                        it.outputIconSet.set(layout.buildDirectory.dir("app/app.iconset"))
                        it.outputIcon.set(layout.buildDirectory.file("app/app.icns"))
                        it.sourceIcon.set(app.iconFile)
                    }

                    register(ImageAssets(infoPlistTask, iconTask))
                }
                each<HasUnsignedUiBundle> {
                    prepare { dist, app ->
                        val capitalizedAppName = app.capitalizedAppName
                        dist.rootDirPath.set(capitalizedAppName.map { "$it.app/Contents" })
                        dist.launcherFilePath.set(capitalizedAppName.map { "MacOS/$it" })
                        if (dist.buildType == BuildType.Release) {
                            val releaseDist = app.distributionContainer.add(
                                "release",
                                false,
                                true,
                                dist.canBuildOnHostMachine,
                                dist.targetMachine,
                                dist.buildType,
                                DefaultReleaseDistribution::class.java,
                                ReleaseDistribution::class.java
                            )
                            releaseDist.rootDirPath.set(dist.rootDirPath)
                            releaseDist.launcherFilePath.set(dist.launcherFilePath)
                            releaseDist.distTask.configure { t ->
                                t.unsignedImage.set(dist.outputs.imageDirectory)
                                t.signingIdentity.set(app.signingIdentity)
                                t.notarizationProfileName.set(app.notarizationProfileName)
                            }
                        }
                    }
                    require<ImageAssets> {
                        derive { dist, app, assets ->

                            dist.withImage {
                                includeFile("Info.plist", assets.infoPlistTask.flatMap { it.plistFile })
                                includeFile(
                                    app.iconName.map { "Resources/$it" },
                                    assets.iconTask.flatMap {
                                        if (it.sourceIcon.get().asFile.exists()) {
                                            it.outputIcon
                                        } else {
                                            null
                                        }
                                    })
                            }
                        }
                    }
                }
            }

            applications.withApp<DefaultUiApplication> { app ->
                app.iconFile.set(layout.projectDirectory.file("src/main/Icon1024.png"))

                app.notarizationProfileName.set(envVar("APP_NOTARIZATION_PROFILE"))
                app.signingIdentity.set(envVar("APP_SIGNING_IDENTITY"))
            }
        }
    }

    private fun Project.envVar(name: String): Provider<String> {
        return providers.environmentVariable(name)
    }
}

private class ImageAssets(val infoPlistTask: TaskProvider<InfoPlist>, val iconTask: Provider<AppIcon>)