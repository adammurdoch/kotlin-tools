package net.rubygrapefruit.bytecode

interface Decoder {
    fun u1(): UByte

    fun u2(): UInt

    fun u4(): UInt

    fun string(): String

    fun skip(count: Int)
}