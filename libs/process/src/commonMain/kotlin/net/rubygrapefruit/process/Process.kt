package net.rubygrapefruit.process

interface Process {
    /**
     * Blocks until the process completes.
     */
    fun waitFor()

    companion object {
        /**
         * Starts a process with the given command-line. The command is interpreted as if provided to the shell.
         */
        fun start(commandLine: List<String>, config: ProcessBuilder.() -> Unit = {}): Process {
            val builder = DefaultProcessBuilder()
            builder.commandLine(commandLine)
            config(builder)
            return builder.start()
        }
    }
}