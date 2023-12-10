package net.rubygrapefruit.process

interface Process<T> {
    /**
     * Blocks until the process completes.
     */
    fun waitFor(): T

    companion object {
        /**
         * Starts a process with the given command-line. The command is interpreted as if provided to the shell.
         */
        fun start(commandLine: List<String>): Process<Unit> {
            return command(commandLine).start()
        }

        /**
         * Returns a process builder for the given command-line. The command is interpreted as if provided to the shell.
         */
        fun command(commandLine: List<String>): ProcessBuilder {
            return DefaultProcessBuilder(commandLine)
        }

        /**
         * Returns a process builder for the given command-line. The command is interpreted as if provided to the shell.
         */
        fun command(vararg commandLine: String): ProcessBuilder {
            return command(commandLine.toList())
        }
    }
}