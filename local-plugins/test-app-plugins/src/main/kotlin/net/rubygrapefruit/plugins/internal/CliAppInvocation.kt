package net.rubygrapefruit.plugins.internal

import net.rubygrapefruit.machine.info.Machine
import net.rubygrapefruit.plugins.stage0.BuildConstants
import java.nio.file.Path
import kotlin.io.path.absolutePathString

sealed interface CliAppInvocation {
    /**
     * The file that is used to run the app.
     */
    val launcher: Path

    val commandLine: List<String>

    val expectedOutput: String?
}

sealed class AbstractScriptInvocation(
    val script: Path,
    val args: List<String>,
    override val expectedOutput: String?
) : CliAppInvocation {
    override val launcher: Path
        get() = script

    override val commandLine: List<String>
        get() = listOf(script.absolutePathString()) + args
}

class ScriptInvocation(
    script: Path,
    args: List<String>,
    expectedOutput: String?
) : AbstractScriptInvocation(script, args, expectedOutput) {
    companion object {
        fun of(name: String, distDir: Path, launcher: String?, args: List<String>, expectedOutput: String?): ScriptInvocation {
            val scriptPath = Machine.thisMachine.scriptName(launcher ?: name)
            return ScriptInvocation(distDir.resolve(scriptPath), args, expectedOutput)
        }
    }
}

class ScriptInvocationWithInstalledJvm(
    script: Path,
    args: List<String>,
    expectedOutput: String?,
    val jvmVersion: Int
) : AbstractScriptInvocation(script, args, expectedOutput) {
    companion object {
        fun of(name: String, distDir: Path, launcher: String?, args: List<String>, expectedOutput: String?, jvmVersion: Int?): ScriptInvocationWithInstalledJvm {
            val scriptPath = Machine.thisMachine.scriptName(launcher ?: name)
            return ScriptInvocationWithInstalledJvm(distDir.resolve(scriptPath), args, expectedOutput, jvmVersion ?: BuildConstants.constants.apps.jvm.version)
        }
    }
}

class BinaryInvocation(
    val binary: Path,
    val args: List<String>,
    override val expectedOutput: String?
) : CliAppInvocation {
    override val launcher: Path
        get() = binary

    override val commandLine: List<String>
        get() = listOf(binary.absolutePathString()) + args

    companion object {
        fun of(name: String, distDir: Path, launcher: String?, args: List<String>, expectedOutput: String?): BinaryInvocation {
            val binPath = Machine.thisMachine.executableName(launcher ?: name)
            return BinaryInvocation(distDir.resolve(binPath), args, expectedOutput)
        }
    }
}
