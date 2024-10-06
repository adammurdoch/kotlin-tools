package net.rubygrapefruit.io.codec

interface Decoder {
    fun hasMore(): Boolean

    /**
     * Decodes a [UByte]
     */
    fun ubyte(): UByte

    /**
     * Decodes a [UShort]
     */
    fun ushort(): UShort

    /**
     * Decodes a [Byte]
     */
    fun byte(): Byte

    /**
     * Decodes an [Int]
     */
    fun int(): Int

    /**
     * Decodes a [Long]
     */
    fun long(): Long

    /**
     * Decodes a byte array.
     */
    fun bytes(): ByteArray

    /**
     * Decodes a string
     */
    fun string(): String
}