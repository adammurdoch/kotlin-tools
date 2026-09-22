package sample.lib.jvm.generated

class JvmLog {
    fun log() {
        val message = JvmLog::class.java.classLoader.getResource("message.txt")!!.readText()
        GeneratedJvm().log(message)
    }
}