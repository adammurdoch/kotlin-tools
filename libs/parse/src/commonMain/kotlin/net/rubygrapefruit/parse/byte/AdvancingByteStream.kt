package net.rubygrapefruit.parse.byte

import net.rubygrapefruit.parse.AdvancingInput

internal interface AdvancingByteStream: ByteStream, AdvancingInput<BytePosition>
