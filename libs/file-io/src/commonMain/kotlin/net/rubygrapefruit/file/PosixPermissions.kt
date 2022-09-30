package net.rubygrapefruit.file

import kotlin.jvm.JvmInline

@JvmInline
value class PosixPermissions internal constructor(internal val mode: UInt) {
    companion object {
        internal const val ownerRead = 0x100u
        internal const val ownerWrite = 0x80u
        internal const val ownerExecute = 0x40u
        internal const val groupRead = 0x20u
        internal const val groupWrite = 0x10u
        internal const val groupExecute = 0x8u
        internal const val othersRead = 0x4u
        internal const val othersWrite = 0x2u
        internal const val othersExecute = 0x1u

        /**
         * Read-only by user, group and others.
         */
        val readOnlyFile: PosixPermissions
            get() = PosixPermissions(ownerRead or groupRead or othersRead)

        /**
         * Read and execute by user, group and others.
         */
        val readOnlyDirectory: PosixPermissions
            get() = PosixPermissions(ownerRead or ownerExecute or groupRead or groupExecute or othersRead or othersExecute)

        /**
         * Read-write-execute by user and read-execute by group and others.
         */
        val readWriteDirectory: PosixPermissions
            get() = PosixPermissions(ownerRead or ownerWrite or ownerExecute or groupRead or groupExecute or othersRead or othersExecute)

        /**
         * Read-write by user and read by group and others.
         */
        val readWriteFile: PosixPermissions
            get() = PosixPermissions(ownerRead or ownerWrite or groupRead or othersRead)
    }

    override fun toString(): String {
        return mode.toString(8).padStart(3, '0')
    }
}