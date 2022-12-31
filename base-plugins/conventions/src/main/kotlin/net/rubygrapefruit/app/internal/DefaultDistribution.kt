package net.rubygrapefruit.app.internal

import net.rubygrapefruit.app.Distribution
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import javax.inject.Inject


abstract class DefaultDistribution @Inject constructor(factory: ObjectFactory) : Distribution {
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
}
