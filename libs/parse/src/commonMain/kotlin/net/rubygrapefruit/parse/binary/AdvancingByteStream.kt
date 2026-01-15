package net.rubygrapefruit.parse.binary

import net.rubygrapefruit.parse.AdvancingInput

internal interface AdvancingByteStream: ByteStream, AdvancingInput<BytePosition>
