package sample.lib.kmp.generated

class KmpLog {
    fun log() {
        GeneratedCommon().log()
        val message = KmpLog::class.java.classLoader.getResource("kmp-lib-message.txt")!!.readText()
        println(message)
        GeneratedJvm().log()
    }
}