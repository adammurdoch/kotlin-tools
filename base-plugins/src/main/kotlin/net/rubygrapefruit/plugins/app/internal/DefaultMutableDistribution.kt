package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.Distribution
import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import javax.inject.Inject


abstract class DefaultMutableDistribution @Inject constructor(
    override val name: String,
    override val canBuildOnHostMachine: Boolean,
    val defaultDist: Provider<Distribution>,
    factory: ObjectFactory,
) : Distribution, MutableDistribution {
    companion object {
        fun taskName(distName: String, taskName: String): String {
            return "$distName${taskName.capitalize()}"
        }
    }

    override val outputs: Distribution.Outputs = object : Distribution.Outputs {
        override val imageDirectory: Provider<Directory>
            get() = imageOutputDirectory

        override val launcherFile: Provider<RegularFile>
            get() = launcherOutputFile
    }

    override val launcherFile: RegularFileProperty = factory.fileProperty()

    override val launcherFilePath: Property<String> = factory.property(String::class.java)

    override val imageOutputDirectory: DirectoryProperty = factory.directoryProperty()

    override val launcherOutputFile: RegularFileProperty = factory.fileProperty()

    override val effectiveLauncherFilePath: Provider<String>
        get() {
            return providers.zip(rootDirPath, launcherFilePath) { a, b -> "$a/$b" }
        }

    override val imageBaseDirName: Provider<String>
        get() {
            return defaultDist.map {
                if (this == it) {
                    "dist"
                } else {
                    "dist-images/$name"
                }
            }.orElse("dist-images/$name")
        }

    @get:Inject
    abstract val providers: ProviderFactory

    override fun taskName(base: String): String {
        return taskName(name, base)
    }

    override fun buildDirName(baseName: String): String {
        return "$baseName/$name"
    }
}
