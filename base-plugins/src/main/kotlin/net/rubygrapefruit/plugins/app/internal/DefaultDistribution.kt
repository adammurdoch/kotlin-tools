package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.Distribution
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import javax.inject.Inject


abstract class DefaultDistribution @Inject constructor(
    val name: String,
    val isDefault: Boolean,
    val canBuildForHostMachine: Boolean,
    factory: ObjectFactory
) : Distribution {
    /**
     * The launcher file to copy into the distribution image.
     */
    val launcherFile: RegularFileProperty = factory.fileProperty()

    /**
     * The location in the distribution image to copy the launcher file to.
     */
    val launcherFilePath: Property<String> = factory.property(String::class.java)

    override val imageOutputDirectory: DirectoryProperty = factory.directoryProperty()

    override val launcherOutputFile: RegularFileProperty = factory.fileProperty()

    fun name(base: String): String {
        return if (isDefault) {
            base
        } else {
            "$name-$base"
        }
    }
}
