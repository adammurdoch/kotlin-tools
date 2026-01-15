package net.rubygrapefruit.parse.text

import net.rubygrapefruit.parse.AdvancingInput

internal interface AdvancingCharStream : CharStream, AdvancingInput<CharPosition>