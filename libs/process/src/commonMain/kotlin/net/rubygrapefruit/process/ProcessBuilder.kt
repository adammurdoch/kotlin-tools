package net.rubygrapefruit.process

import kotlinx.io.Sink
import kotlinx.io.Source
import kotlinx.io.buffered
import kotlinx.io.readString
import net.rubygrapefruit.file.Directory

interface ProcessBuilder : ProcessStarter<Unit> {
    /**
     * Specifies the directory to start the process in. Defaults to the current directory of this process.
     */
    fun directory(dir: Directory): ProcessBuilder

    /**
     * Starts the process, inheriting stdout, stderr and stdin from this process and failing when the process finishes
     * with a non-zero exit code.
     */
    override fun start(): Process<Unit>

    /**
     * Collect the output of the child process into a String.
     */
    fun collectOutput(): ProcessStarter<String>

    /**
     * Collect the exit code of the child process.
     */
    fun collectExitCode(): ProcessStarter<Int>

    /**
     * Uses the given action to read from and write to the child process to produce a result.
     */
    fun <T> withInputAndOutput(action: (Source, Sink) -> T): ProcessStarter<T>
}

interface ProcessStarter<T> {
    fun start(): Process<T>
}

internal class DefaultProcessBuilder(private val commandLine: List<String>) : ProcessBuilder {
    private var dir: Directory? = null

    override fun directory(dir: Directory): ProcessBuilder {
        this.dir = dir
        return this
    }

    override fun start(): Process<Unit> {
        return ProcessWithNoResult(start(ProcessStartSpec(commandLine, dir, false, false, true)))
    }

    override fun collectOutput(): ProcessStarter<String> {
        val spec = ProcessStartSpec(commandLine, dir, true, false, true)
        return object : ProcessStarter<String> {
            override fun start(): Process<String> {
                return ProcessWithStringResult(start(spec))
            }
        }
    }

    override fun collectExitCode(): ProcessStarter<Int> {
        val spec = ProcessStartSpec(commandLine, dir, false, false, false)
        return object : ProcessStarter<Int> {
            override fun start(): Process<Int> {
                return ProcessWithExitCodeResult(start(spec))
            }
        }
    }

    override fun <T> withInputAndOutput(action: (Source, Sink) -> T): ProcessStarter<T> {
        val spec = ProcessStartSpec(commandLine, dir, true, true, false)
        return object : ProcessStarter<T> {
            override fun start(): Process<T> {
                return ProcessWithInputAndOutput(action, start(spec))
            }
        }
    }
}

internal abstract class AbstractProcess<T>(protected val control: ProcessControl) : Process<T>

internal class ProcessWithNoResult(control: ProcessControl) : AbstractProcess<Unit>(control) {
    override fun waitFor() {
        control.waitFor()
    }
}

internal class ProcessWithExitCodeResult(control: ProcessControl) : AbstractProcess<Int>(control) {
    override fun waitFor(): Int {
        return control.waitFor()
    }
}

internal class ProcessWithStringResult(control: ProcessControl) : AbstractProcess<String>(control) {
    override fun waitFor(): String {
        val stdout = control.stdout.buffered().readString()
        control.waitFor()
        return stdout
    }
}

internal class ProcessWithInputAndOutput<T>(private val action: (Source, Sink) -> T, control: ProcessControl) :
    AbstractProcess<T>(control) {
    override fun waitFor(): T {
        val stdin = control.stdin.buffered()
        val result = action(control.stdout.buffered(), stdin)
        stdin.flush()
        control.waitFor()
        return result
    }
}
