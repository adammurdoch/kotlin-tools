package net.rubygrapefruit.io.codec

interface Encoder {
    /**
     * Encodes the given [UShort] value.
     *
     * @return this
     */
    fun ushort(value: UShort): Encoder

    /**
     * Encodes the given string.
     *
     * @return this
     */
    fun string(value: String): Encoder
}