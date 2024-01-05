package net.rubygrapefruit.io.codec

interface Decoder {
    /**
     * Decodes a [UShort]
     */
    fun ushort(): UShort

    /**
     * Decodes an [Int]
     */
    fun int(): Int

    /**
     * Decodes a [String]
     */
    fun string(): String
}