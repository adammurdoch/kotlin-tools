@file:OptIn(ExperimentalStdlibApi::class)

package net.rubygrapefruit.store

interface ContentVisitor {
    fun value(name: String, details: ValueInfo) {}

    class ValueInfo(private val offset: Long) {
        val address: String
            get() = "0x" + offset.toHexString(HexFormat.UpperCase)
    }
}
