package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.Dependencies
import net.rubygrapefruit.plugins.app.NativeComponent
import javax.inject.Inject

abstract class DefaultNativeComponent @Inject constructor(
    private val sourceSets: SourceSets,
    private val mainSourceSetName: String
) : NativeComponent<Dependencies> {

    override fun dependencies(config: Dependencies.() -> Unit) {
        sourceSets.withSourceSet(mainSourceSetName) { mainSourceSet, _ ->
            mainSourceSet.dependencies {
                KotlinHandlerBackedDependencies(this).config()
            }
        }
    }

    fun attach() {
        sourceSets.withSourceSet(mainSourceSetName) { mainSourceSet, _ ->
            mainSourceSet.kotlin.srcDirs(generatedSource)
        }
    }
}