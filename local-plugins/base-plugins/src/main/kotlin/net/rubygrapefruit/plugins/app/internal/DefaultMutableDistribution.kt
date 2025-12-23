package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.Distribution
import net.rubygrapefruit.strings.capitalized
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import javax.inject.Inject


abstract class DefaultMutableDistribution @Inject constructor(
    override val name: String,
    override val canBuildOnHostMachine: Boolean
) : Distribution, MutableDistribution {
    companion object {
        fun taskName(distName: String, taskName: String): String {
            return "$distName${taskName.capitalized()}"
        }
    }

    override val outputs: Distribution.Outputs = DefaultDistributionOutputs(imageOutputDirectory, launcherOutputFile)

    override val effectiveLauncherFilePath: Provider<String>
        get() {
            return providers.zip(rootDirPath, launcherFilePath) { a, b -> "$a/$b" }
        }

    @get:Inject
    abstract val providers: ProviderFactory

    override fun taskName(baseName: String): String {
        return taskName(name, baseName)
    }

    override fun buildDirName(baseName: String): String {
        return "$baseName/$name"
    }
}
