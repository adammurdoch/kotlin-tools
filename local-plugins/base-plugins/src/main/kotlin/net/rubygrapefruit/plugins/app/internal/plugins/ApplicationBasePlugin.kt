package net.rubygrapefruit.plugins.app.internal.plugins

import net.rubygrapefruit.plugins.app.internal.ApplicationRegistry
import net.rubygrapefruit.plugins.app.internal.HasDistributionImage
import net.rubygrapefruit.plugins.app.internal.MultiPlatformComponentRegistry
import net.rubygrapefruit.plugins.app.internal.MutableApplication
import net.rubygrapefruit.plugins.app.internal.component.ComponentRegistry
import org.gradle.api.Plugin
import org.gradle.api.Project

class ApplicationBasePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            applyBasePlugin()

            repositories.mavenCentral()

            target.extensions.create("multiplatformComponents", MultiPlatformComponentRegistry::class.java)
            val componentRegistry = target.extensions.create("componentRegistry", ComponentRegistry::class.java)
            target.extensions.create("applicationRegistry", ApplicationRegistry::class.java, componentRegistry)

            componentRegistry.from<MutableApplication> {
                derive { app ->
                    app.distributionContainer.each {
                        register(this)
                    }
                }

                from<HasDistributionImage> {
                    prepare { dist, _ ->
                        dist.distTask.configure { t ->
                            t.includeFile(dist.launcherFilePath, dist.launcherFile)
                        }
                    }
                }
            }
        }
    }
}