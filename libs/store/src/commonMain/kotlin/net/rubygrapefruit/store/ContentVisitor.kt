@file:OptIn(ExperimentalStdlibApi::class)

package net.rubygrapefruit.store

interface ContentVisitor {
    fun value(name: String, details: ValueInfo) {}

    class ValueInfo internal constructor(private val address: Address) {
        val formatted: String
            get() = "0x" + address.offset.toHexString(HexFormat.UpperCase)
    }
}
