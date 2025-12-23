package net.rubygrapefruit.bytecode

internal enum class AccessFlag(val value: UInt) {
    Public(0x1u),
    Private(0x2u),
    Protected(0x4u),
    Static(0x8u),
    Final(0x10u),
    Synchronized(0x20u),
    Bridge(0x40u),
    Varargs(0x80u),
    Native(0x100u),
    Volatile(0x40u),
    Transient(0x80u),
    Interface(0x200u),
    Abstract(0x400u),
    Synthetic(0x1000u),
    Annotation(0x2000u),
    Enum(0x4000u),
    Module(0x8000u);

    fun containedIn(accessFlags: UInt): Boolean {
        return value and accessFlags != 0u
    }
}