package net.rubygrapefruit.app.plugins

import net.rubygrapefruit.app.internal.DefaultJvmUiApplication
import net.rubygrapefruit.app.internal.applications
import net.rubygrapefruit.app.tasks.*
import org.gradle.api.Plugin
import org.gradle.api.Project

class JvmUiApplicationPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins.apply(JvmApplicationBasePlugin::class.java)
            plugins.apply(EmbeddedJvmLauncherPlugin::class.java)

            applications.withApp<DefaultJvmUiApplication> { app ->
                app.iconFile.set(layout.projectDirectory.file("src/main/Icon1024.png"))

                val capitalizedAppName = app.appName.map { it.replaceFirstChar { it.uppercase() } }
                val iconName = capitalizedAppName.map { "$it.icns" }

                val infoPlistTask = tasks.register("infoPlist", InfoPlist::class.java) {
                    it.plistFile.set(layout.buildDirectory.file("app/Info.plist"))
                    it.bundleName.set(capitalizedAppName)
                    it.bundleIdentifier.set(capitalizedAppName)
                    it.executableName.set(capitalizedAppName)
                    it.iconName.set(iconName)
                }
                val configTask = tasks.register("launcherConf", LauncherConf::class.java) {
                    it.configFile.set(layout.buildDirectory.file("app/launcher.conf"))
                    it.applicationDisplayName.set(capitalizedAppName)
                    it.iconName.set(iconName)
                    it.javaCommand.set(app.distribution.javaLauncherPath)
                    it.module.set(app.module.name)
                    it.mainClass.set(app.mainClass)
                }
                val launcherTask = tasks.register("nativeLauncher", NativeUiLauncher::class.java) {
                    it.outputFile.set(layout.buildDirectory.file("app/native-launcher.kexe"))
                }
                val iconTask = tasks.register("appIcon", AppIcon::class.java) {
                    it.outputIconSet.set(layout.buildDirectory.dir("app/app.iconset"))
                    it.outputIcon.set(layout.buildDirectory.file("app/app.icns"))
                    it.sourceIcon.set(app.iconFile)
                }

                app.distribution.launcherFilePath.set(capitalizedAppName.map { "MacOS/$it" })
                app.distribution.launcherFile.set(launcherTask.flatMap { it.outputFile })

                applications.applyToDistribution { dist ->
                    dist.imageDirectory.set(layout.buildDirectory.dir(capitalizedAppName.map { "debug/$it.app" }))
                    dist.rootDirPath.set("Contents")
                    dist.includeFile("Info.plist", infoPlistTask.flatMap { it.plistFile })
                    dist.includeFile("Resources/launcher.conf", configTask.flatMap { it.configFile })
                    dist.includeFile(
                        iconName.map { "Resources/$it" },
                        iconTask.flatMap { if (it.sourceIcon.get().asFile.exists()) it.outputIcon else null })
                }

                tasks.register("releaseDist", ReleaseDistribution::class.java) { t ->
                    t.imageDirectory.set(layout.buildDirectory.dir(capitalizedAppName.map { "release/$it.app" }))
                    t.unsignedImage.set(app.distribution.imageOutputDirectory)
                    t.signingIdentity.set(app.signingIdentity)
                    t.notarizationProfileName.set(app.notarizationProfileName)
                }
            }

            val app = extensions.create("application", DefaultJvmUiApplication::class.java)
            applications.register(app)
        }
    }
}