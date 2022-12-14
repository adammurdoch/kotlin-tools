package net.rubygrapefruit.bytecode

interface Decoder {
    val offset: Long

    fun u1(): UByte

    fun u2(): UInt

    fun u4(): UInt

    fun i4(): Int

    fun string(): String

    fun skip(count: Int)
}