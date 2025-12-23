package net.rubygrapefruit.bytecode

internal interface Encoder {
    fun u1(value: UByte)

    fun u2(value: UInt)

    fun u4(value: UInt)

    fun i4(value: Int)

    fun string(string: String)

    fun bytes(bytes: ByteArray)

    fun writing(builder: Encoder.() -> Unit): ByteArray
}