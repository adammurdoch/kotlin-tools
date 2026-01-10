package net.rubygrapefruit.parse.byte

internal fun format(byte: Byte): String {
    return 'x' + byte.toString(16).padStart(2, '0')
}
