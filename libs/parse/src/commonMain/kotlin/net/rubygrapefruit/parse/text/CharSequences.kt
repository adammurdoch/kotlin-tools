package net.rubygrapefruit.parse.text

internal fun CharSequence.posAt(startPos: CharPosition, index: Int): CharPosition {
    var line = startPos.line
    var col = startPos.col

    for (i in 0 until index) {
        if (get(i) == '\n') {
            line++
            col = 1
        } else {
            col++
        }
    }

    return CharPosition(startPos.offset + index, line, col)
}
