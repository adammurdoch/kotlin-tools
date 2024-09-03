package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.BuildType
import net.rubygrapefruit.plugins.app.Distribution
import net.rubygrapefruit.plugins.app.NativeMachine
import net.rubygrapefruit.plugins.app.internal.tasks.DistributionImage
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider
import javax.inject.Inject


abstract class DefaultDistribution @Inject constructor(
    override val name: String,
    val canBuildOnHostMachine: Boolean,
    val targetMachine: NativeMachine?,
    val buildType: BuildType,
    val distTask: TaskProvider<DistributionImage>,
    val defaultDist: Provider<Distribution>,
    factory: ObjectFactory
) : Distribution, MutableDistribution {
    companion object {
        fun taskName(distName: String, taskName: String): String {
            return "$distName${taskName.capitalize()}"
        }
    }

    override val launcherFile: RegularFileProperty = factory.fileProperty()

    override val launcherFilePath: Property<String> = factory.property(String::class.java)

    override val imageOutputDirectory: DirectoryProperty = factory.directoryProperty()

    override val launcherOutputFile: RegularFileProperty = factory.fileProperty()

    val imageBaseDir: Provider<String>
        get() {
            return defaultDist.map {
                if (this == it) {
                    "dist"
                } else {
                    "dist-images/$name"
                }
            }.orElse("dist-images/$name")
        }

    override fun taskName(base: String): String {
        return taskName(name, base)
    }

    override fun buildDirName(baseName: String): String {
        return "$baseName/$name"
    }

    override fun withImage(action: DistributionImage.() -> Unit) {
        distTask.configure(action)
    }
}
