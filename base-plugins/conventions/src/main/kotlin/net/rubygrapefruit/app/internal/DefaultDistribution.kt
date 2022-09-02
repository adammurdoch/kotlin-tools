package net.rubygrapefruit.app.internal

import net.rubygrapefruit.app.Distribution
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import javax.inject.Inject


abstract class DefaultDistribution @Inject constructor(factory: ObjectFactory) : Distribution {
    override val imageOutputDirectory: DirectoryProperty = factory.directoryProperty()

    override val launcherOutputFile: RegularFileProperty = factory.fileProperty()
}
