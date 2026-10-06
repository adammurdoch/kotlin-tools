package net.rubygrapefruit.plugins.app.internal.plugins

import net.rubygrapefruit.plugins.app.BuildType
import net.rubygrapefruit.plugins.app.NativeMachine
import net.rubygrapefruit.plugins.app.Versions
import net.rubygrapefruit.plugins.app.internal.DefaultJvmUiAppDistribution
import net.rubygrapefruit.plugins.app.internal.DefaultJvmUiApplication
import net.rubygrapefruit.plugins.app.internal.HostMachine
import net.rubygrapefruit.plugins.app.internal.componentRegistry
import net.rubygrapefruit.plugins.app.internal.tasks.LauncherConf
import net.rubygrapefruit.plugins.app.internal.tasks.NativeLauncher
import net.rubygrapefruit.plugins.app.internal.tasks.NativeUiLauncher
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.attributes.Usage
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.jvm.tasks.Jar
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

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
                    val generatorTask = tasks.register("generateLauncher", NativeLauncher::class.java) {
                        it.sourceDirectory.set(layout.buildDirectory.dir("generated/ui-launcher"))
                        it.packageName.set("ui")
                        it.delegateMethod.set(app.mainClass.map { mainClass -> "${mainClass.substringBeforeLast('.')}.main" })
                    }

                    val sourceSets = extensions.getByType(SourceSetContainer::class.java)

                    val sourceSet = sourceSets.create("launcher")
                    sourceSet.java.setSrcDirs(emptyList<String>())
                    sourceSet.resources.setSrcDirs(emptyList<String>())

                    val configuration = configurations.create("launcherRuntime")
                    dependencies.add(configuration.name, Versions.libs.coordinates("ui-app-bootstrap"))

                    val kotlin = extensions.getByType(KotlinJvmProjectExtension::class.java)
                    val kotlinSourceSet = kotlin.sourceSets.getByName("launcher")
                    kotlinSourceSet.kotlin.setSrcDirs(emptyList<String>())
                    kotlinSourceSet.generatedKotlin.srcDir(generatorTask.flatMap { it.sourceDirectory })
                    kotlinSourceSet.dependencies {
                        implementation(sourceSets.getByName("main").output)
                    }
                    configurations.getByName(kotlinSourceSet.implementationConfigurationName).extendsFrom(configuration)

                    tasks.named("jar", Jar::class.java) {
                        it.from(sourceSet.output)
                    }

                    app.module.requires.add("net.rubygrapefruit.ui_app_bootstrap")
                    app.runtimeModulePath.from(configuration)

                    val machine = NativeMachine.MacOSArm64
                    // TODO - 'can build' flag is incorrect - it depends on the JVM to be embedded
                    val canBuild = HostMachine.current.canBeBuilt && HostMachine.current.machine == machine
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
                        val nativeBinary = configurations.create("nativeBinaries${dist.targetMachine.name}") {
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
                            it.iconName.set(app.iconName)
                            it.javaCommand.set(dist.javaLauncherPath)
                            it.module.set(app.module.name)
                            it.mainClass.set("ui.MainKt")
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