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

    val expectedOutput: List<String>
}

sealed class AbstractScriptInvocation(
    val script: Path,
    val args: List<String>,
    override val expectedOutput: List<String>
) : CliAppInvocation {
    override val launcher: Path
        get() = script

    override val commandLine: List<String>
        get() = listOf(script.absolutePathString()) + args
}

class ScriptInvocation(
    script: Path,
    args: List<String>,
    expectedOutput: List<String>
) : AbstractScriptInvocation(script, args, expectedOutput) {
    companion object {
        fun of(name: String, distDir: Path, launcher: String?, args: List<String>, expectedOutput: List<String>): ScriptInvocation {
            val scriptPath = Machine.thisMachine.scriptName(launcher ?: name)
            return ScriptInvocation(distDir.resolve(scriptPath), args, expectedOutput)
        }
    }
}

class ScriptInvocationWithSystemJvm(
    script: Path,
    args: List<String>,
    expectedOutput: List<String>,
    val jvmVersion: Int
) : AbstractScriptInvocation(script, args, expectedOutput) {
    companion object {
        fun of(name: String, distDir: Path, launcher: String?, args: List<String>, expectedOutput: List<String>, jvmVersion: Int?): ScriptInvocationWithSystemJvm {
            val scriptPath = Machine.thisMachine.scriptName(launcher ?: name)
            return ScriptInvocationWithSystemJvm(distDir.resolve(scriptPath), args, expectedOutput, jvmVersion ?: BuildConstants.constants.apps.jvm.version)
        }
    }
}

class BinaryInvocation(
    val binary: Path,
    val args: List<String>,
    override val expectedOutput: List<String>
) : CliAppInvocation {
    override val launcher: Path
        get() = binary

    override val commandLine: List<String>
        get() = listOf(binary.absolutePathString()) + args

    companion object {
        fun of(name: String, distDir: Path, launcher: String?, args: List<String>, expectedOutput: List<String>): BinaryInvocation {
            val binPath = Machine.thisMachine.executableName(launcher ?: name)
            return BinaryInvocation(distDir.resolve(binPath), args, expectedOutput)
        }
    }
}
