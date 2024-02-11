package net.rubygrapefruit.io.codec

interface Decoder {
    /**
     * Decodes a [UByte]
     */
    fun ubyte(): UByte

    /**
     * Decodes a [UShort]
     */
    fun ushort(): UShort

    /**
     * Decodes an [Int]
     */
    fun int(): Int

    /**
     * Decodes a [Long]
     */
    fun long(): Long

    /**
     * Decodes a [String]
     */
    fun string(): String
}