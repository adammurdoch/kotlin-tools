package net.rubygrapefruit.plugins.app.internal.plugins

import net.rubygrapefruit.plugins.app.BuildType
import net.rubygrapefruit.plugins.app.Versions
import net.rubygrapefruit.plugins.app.internal.*
import net.rubygrapefruit.plugins.app.internal.tasks.LauncherConf
import net.rubygrapefruit.plugins.app.internal.tasks.NativeLauncher
import net.rubygrapefruit.plugins.app.internal.tasks.NativeUiLauncher
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.attributes.Usage
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

@OptIn(ExperimentalKotlinGradlePluginApi::class)
@Suppress("unused")
class JvmUiApplicationPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins.apply(JvmApplicationBasePlugin::class.java)
            plugins.apply(EmbeddedJvmLauncherPlugin::class.java)
            plugins.apply(UiApplicationBasePlugin::class.java)

            componentRegistry.each<DefaultJvmUiApplication> {
                derive { app ->
                    val generatorTask = tasks.register("jvmUiLauncher", NativeLauncher::class.java) {
                        it.sourceDirectory.set(layout.buildDirectory.dir("generated/jvm-ui-launcher"))
                        it.packageName.set("ui")
                        it.delegateMethod.set(app.mainClass.map { mainClass -> "${mainClass.substringBeforeLast('.')}.main" })
                    }

                    val sourceSet = addGeneratedProductionSourceSet("jvmUiLauncher")

                    val configuration = configurations.create("jvmUiLauncherRuntime")
                    dependencies.add(configuration.name, Versions.libs.coordinates("ui-app-bootstrap"))

                    val kotlinSourceSet = sourceSet.kotlinSourceSet
                    kotlinSourceSet.generatedKotlin.srcDir(generatorTask.flatMap { it.sourceDirectory })
                    configurations.getByName(kotlinSourceSet.implementationConfigurationName).extendsFrom(configuration)

                    app.startOnMainThread.convention(false)
                    app.module.requires.add("net.rubygrapefruit.ui_app_bootstrap")
                    app.runtimeModulePath.from(configuration)

                    val machine = MacOsArm64
                    // TODO - 'can build' flag is incorrect - it depends on the JVM to be embedded
                    val canBuild = NativeMachine.current == machine
                    val dist = app.distributionContainer.add(
                        "unsignedRelease",
                        true,
                        false,
                        canBuild,
                        machine,
                        BuildType.Release,
                        DefaultJvmUiAppDistribution::class.java
                    )
                    register(dist)
                }

                each<DefaultJvmUiAppDistribution> {
                    derive { dist, app ->
                        val nativeBinary = configurations.create("nativeBinaries${dist.targetMachine.alias}") {
                            it.attributes.attribute(
                                Usage.USAGE_ATTRIBUTE,
                                objects.named(Usage::class.java, "native-binary-${dist.targetMachine.kotlinTarget}")
                            )
                            it.isCanBeResolved = true
                            it.isCanBeConsumed = false
                        }
                        dependencies.add(nativeBinary.name, "net.rubygrapefruit.plugins:native-jvm-launcher:1.0-dev")

                        val launcherTask = tasks.register(dist.taskName("nativeLauncher"), NativeUiLauncher::class.java) {
                            it.inputFile.set(layout.file(nativeBinary.elements.map { it.first().asFile }))
                            it.outputFile.set(layout.buildDirectory.file(dist.buildDirName("native-launcher") + "/native-launcher.kexe"))
                        }

                        val configTask = tasks.register(dist.taskName("launcherConf"), LauncherConf::class.java) {
                            it.configFile.set(layout.buildDirectory.file(dist.buildDirName("launcher-config") + "/launcher.conf"))
                            it.applicationDisplayName.set(app.capitalizedAppName)
                            it.jvmDirPath.set("jvm")
                            it.mainClass.set("ui.MainKt")
                            it.startOnMainThread.set(app.startOnMainThread)
                        }

                        dist.launcherFile.set(launcherTask.flatMap { it.outputFile })
                        dist.withImage {
                            includeFile("Resources/launcher.conf", configTask.flatMap { it.configFile })
                        }
                    }
                }
            }

            val app = extensions.create("application", DefaultJvmUiApplication::class.java)
            componentRegistry.register(app)
        }
    }
}