package net.rubygrapefruit.io.codec

interface Encoder {
    /**
     * Encodes the given [UByte] value.
     *
     * @return this
     */
    fun ubyte(value: UByte): Encoder

    /**
     * Encodes the given [UShort] value.
     *
     * @return this
     */
    fun ushort(value: UShort): Encoder

    /**
     * Encodes the given [Byte] value.
     *
     * @return this
     */
    fun byte(value: Byte): Encoder

    /**
     * Encodes the given [Int] value.
     *
     * @return this
     */
    fun int(value: Int): Encoder

    /**
     * Encodes the given [Long] value.
     *
     * @return this
     */
    fun long(value: Long): Encoder

    /**
     * Encodes the given byte array, including length information.
     *
     * The value can be read using [Decoder.bytes].
     *
     * @return this
     */
    fun bytes(value: ByteArray): Encoder

    /**
     * Encodes the given string.
     *
     * @return this
     */
    fun string(value: String): Encoder
}