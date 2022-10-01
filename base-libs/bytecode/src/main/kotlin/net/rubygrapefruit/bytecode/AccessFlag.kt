package net.rubygrapefruit.bytecode

internal enum class AccessFlag(val value: UInt) {
    Interface(0x0200u),
    Annotation(0x2000u),
    Enum(0x4000u),
    Module(0x8000u);

    fun containedIn(accessFlags: UInt): Boolean {
        return value and accessFlags != 0u
    }
}