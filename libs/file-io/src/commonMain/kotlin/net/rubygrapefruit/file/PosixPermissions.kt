package net.rubygrapefruit.file

import kotlin.jvm.JvmInline

@JvmInline
value class PosixPermissions internal constructor(internal val mode: UInt) {
    companion object {
        const val ownerRead = 0x100u
        const val ownerWrite = 0x80u
        const val ownerExecute = 0x40u
        const val groupRead = 0x20u
        const val groupWrite = 0x10u
        const val groupExecute = 0x8u
        const val othersRead = 0x4u
        const val othersWrite = 0x2u
        const val othersExecute = 0x1u

        /**
         * Read-only by user, group and others.
         */
        val readOnly: PosixPermissions
            get() = PosixPermissions(ownerRead or groupRead or othersRead)
    }

    override fun toString(): String {
        return mode.toString(8).padStart(3, '0')
    }
}