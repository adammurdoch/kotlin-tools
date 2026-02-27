package sample

import net.rubygrapefruit.file.RegularFile
import net.rubygrapefruit.parse.binary.literal
import net.rubygrapefruit.parse.binary.one
import net.rubygrapefruit.parse.binary.parse
import net.rubygrapefruit.parse.combinators.discard
import net.rubygrapefruit.parse.combinators.map
import net.rubygrapefruit.parse.combinators.sequence
import net.rubygrapefruit.parse.combinators.zeroOrMore

class Parser {
    fun parse(file: RegularFile): ExeDetails {
        val magic64le = literal(byteArrayOf(0xcf.toByte(), 0xfa.toByte(), 0xed.toByte(), 0xfe.toByte()))

        val u16le = sequence(one(), one()) { b3, b4 -> b4.toUByte().toUInt().rotateLeft(8).or(b3.toUByte().toUInt()) }
        val u32le = sequence(u16le, u16le) { w1, w2 -> w2.rotateLeft(16).or(w1) }

        val cpu = sequence(u32le, u32le) { cpu, subtype -> Pair(cpu, subtype) }
        val header = sequence(magic64le, cpu, discard(zeroOrMore(one())))

        val parser = map(header) {
            println("-> CPU: ${it.first.toHexString(HexFormat.UpperCase)}")
            println("-> CPU subtype: ${it.second.toHexString(HexFormat.UpperCase)}")
            ExeDetails()
        }
        return parser.parse(file.readBytes()).get()
    }
}