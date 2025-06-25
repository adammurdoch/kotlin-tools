package net.rubygrapefruit.plugins.app.internal.plugins

import net.rubygrapefruit.plugins.app.BuildType
import net.rubygrapefruit.plugins.app.internal.DefaultReleaseDistribution
import net.rubygrapefruit.plugins.app.internal.DefaultUiApplication
import net.rubygrapefruit.plugins.app.internal.HasUnsignedUiBundle
import net.rubygrapefruit.plugins.app.internal.applications
import net.rubygrapefruit.plugins.app.internal.tasks.AppIcon
import net.rubygrapefruit.plugins.app.internal.tasks.DistributionImage
import net.rubygrapefruit.plugins.app.internal.tasks.InfoPlist
import net.rubygrapefruit.plugins.app.internal.tasks.ReleaseDistribution
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.Provider

class UiApplicationBasePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins.apply(ApplicationBasePlugin::class.java)
            applications.withApp<DefaultUiApplication> { app ->
                val capitalizedAppName = app.capitalizedAppName
                app.iconFile.set(layout.projectDirectory.file("src/main/Icon1024.png"))

                app.notarizationProfileName.set(envVar("APP_NOTARIZATION_PROFILE"))
                app.signingIdentity.set(envVar("APP_SIGNING_IDENTITY"))

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

                app.distributionContainer.eachOfType<HasUnsignedUiBundle> {
                    rootDirPath.set(capitalizedAppName.map { "$it.app/Contents" })
                    launcherFilePath.set(capitalizedAppName.map { "MacOS/$it" })

                    withImage {
                        includeFile("Info.plist", infoPlistTask.flatMap { it.plistFile })
                        includeFile(
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

                    if (buildType == BuildType.Release) {
                        val releaseDist = app.distributionContainer.add(
                            "release",
                            false,
                            false,
                            canBuildOnHostMachine,
                            targetMachine,
                            buildType,
                            DefaultReleaseDistribution::class.java,
                            ReleaseDistribution::class.java
                        )
                        releaseDist.rootDirPath.set(rootDirPath)
                        releaseDist.launcherFilePath.set(launcherFilePath)
                        releaseDist.distTask.configure { t ->
                            t.unsignedImage.set(outputs.imageDirectory)
                            t.signingIdentity.set(app.signingIdentity)
                            t.notarizationProfileName.set(app.notarizationProfileName)
                        }
                    }

                    app.installations.empty()
                }
            }
        }
    }

    private fun Project.envVar(name: String): Provider<String> {
        return providers.environmentVariable(name).orElse(provider { throw RuntimeException("Environment variable '$name' not set") })
    }
}