package net.rubygrapefruit.parse.char

import net.rubygrapefruit.parse.AdvancingInput

internal interface AdvancingCharStream : CharStream, AdvancingInput<CharPosition>