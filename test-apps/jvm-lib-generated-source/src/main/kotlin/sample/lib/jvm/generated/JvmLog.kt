package sample.lib.jvm.generated

class JvmLog {
    fun log() {
        val message = JvmLog::class.java.classLoader.getResource("jvm-lib-message.txt")!!.readText()
        println(message)
        GeneratedJvm().log()
    }
}