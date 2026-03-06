package sample

import net.rubygrapefruit.file.RegularFile
import net.rubygrapefruit.parse.binary.*
import net.rubygrapefruit.parse.combinators.*

class Parser {
    fun parse(file: RegularFile): List<Image> {
        val u16le = uint16LittleEndian()
        val u32le = sequence(u16le, u16le) { w1, w2 -> w2.toUInt().rotateLeft(16).or(w1.toUInt()) }

        val u16be = uint16BigEndian()
        val u32be = sequence(u16be, u16be) { w1, w2 -> w1.toUInt().rotateLeft(16).or(w2.toUInt()) }

        val magic64le = literal(byteArrayOf(0xcf.toByte(), 0xfa.toByte(), 0xed.toByte(), 0xfe.toByte()))

        val cpule = sequence(u32le, u32le) { cpu, subtype -> cpu(cpu, subtype) }
        val header64le = sequence(magic64le, cpule)
        val image64le = map(header64le) { cpu -> listOf(MachOImage(cpu)) }
        val file64le = sequence(image64le, discard(zeroOrMore(one())))

        val magicUniversal = literal(byteArrayOf(0xca.toByte(), 0xfe.toByte(), 0xba.toByte(), 0xbe.toByte()))

        val cpube = sequence(u32be, u32be) { cpu, subtype -> cpu(cpu, subtype) }
        val binaryHeader = sequence(cpube, repeat(3, u32be)) { cpu, _ -> MachOImage(cpu) }

        val binaryHeaders = decide(u32be) { repeat(it.toInt(), binaryHeader) }
        val executables = sequence(magicUniversal, binaryHeaders)
        val fileUniversal = sequence(executables, discard(zeroOrMore(one())))

        val parser = oneOf(file64le, fileUniversal)

        return parser.parse(file.readBytes()).get()
    }

    private fun cpu(cpuType: UInt, subType: UInt): CPU {
        return when (cpuType) {
            0x01000007u -> cpuX64(subType)
            0x0100000Cu -> cpuArm64(subType)
            else -> throw IllegalArgumentException("Unknown CPU type: ${cpuType.toHexString(HexFormat.UpperCase)}")
        }
    }

    private fun cpuX64(subType: UInt): CPU {
        val subTypeStr = when (subType) {
            0x3u -> "All"
            else -> throw IllegalArgumentException("Unknown CPU sub-type: ${subType.toHexString(HexFormat.UpperCase)}")
        }
        return CPU("64-bit x86 - $subTypeStr")
    }

    private fun cpuArm64(subType: UInt): CPU {
        val subTypeStr = when (subType) {
            0u -> "All"
            0x2u, 0x80000002u -> "ARM-A500 and later"
            else -> throw IllegalArgumentException("Unknown CPU sub-type: ${subType.toHexString(HexFormat.UpperCase)}")
        }
        return CPU("64-bit ARM - $subTypeStr")
    }
}