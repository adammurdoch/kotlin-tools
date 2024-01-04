package net.rubygrapefruit.io.codec

interface Decoder {
    /**
     * Decodes a [UShort]
     */
    fun ushort(): UShort

    /**
     * Decodes a [String]
     */
    fun string(): String
}