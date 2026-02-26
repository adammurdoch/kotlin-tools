package net.rubygrapefruit.parse.binary

internal fun format(byte: Byte): String {
    return if (byte == 0.toByte()) {
        "x0"
    } else {
        'x' + byte.toUByte().toString(16).uppercase().padStart(2, '0')
    }
}
