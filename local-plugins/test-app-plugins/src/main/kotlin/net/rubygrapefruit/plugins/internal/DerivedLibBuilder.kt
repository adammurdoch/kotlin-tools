package net.rubygrapefruit.plugins.internal

import net.rubygrapefruit.plugins.stage0.BuildConstants

class DerivedJvmLibBuilder internal constructor(
    val name: String,
    private val container: SampleContainer
) {
    private var jvmVersion = BuildConstants.constants.libs.jvm.version

    fun requiresJvm(version: Int) {
        jvmVersion = version
    }

    internal fun register(derivedFrom: SourceTree?): JvmLib {
        return container.add(name) { name, sampleDir ->
            val sourceDir = derivedFrom.generatedInto(sampleDir, "src/main", "src/test")
            JvmLib(name, sourceDir, jvmVersion)
        }
    }
}

class DerivedKmpLibBuilder internal constructor(
    val name: String,
    private val container: SampleContainer
) {
    private var jvm = true
    private var jvmVersion = BuildConstants.constants.libs.jvm.version

    fun noJvm() {
        jvm = false
    }

    fun requiresJvm(version: Int) {
        jvmVersion = version
    }

    internal fun register(derivedFrom: SourceTree?): KmpLib {
        return container.add(name) { name, sampleDir ->
            val sourceTree = if (derivedFrom == null) {
                OriginSourceTree(
                    sampleDir,
                    mainPath = "src/commonMain",
                    testPath = "src/commonTest",
                    listOf(
                        "src/desktopMain",
                        "src/jvmMain",
                        "src/mingwMain",
                        "src/unixMain",
                        "src/jsMain"
                    )
                )
            } else {
                derivedFrom.generatedInto(sampleDir)
            }
            KmpLib(name, sourceTree, if (jvm) jvmVersion else null)
        }
    }
}