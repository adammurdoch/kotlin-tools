package net.rubygrapefruit.process

interface Process {
    /**
     * Blocks until the process completes.
     */
    fun waitFor()

    companion object {
        fun start(commandLine: List<String>, config: ProcessBuilder.() -> Unit): Process {
            val builder = DefaultProcessBuilder()
            builder.commandLine(commandLine)
            config(builder)
            return builder.start()
        }
    }
}