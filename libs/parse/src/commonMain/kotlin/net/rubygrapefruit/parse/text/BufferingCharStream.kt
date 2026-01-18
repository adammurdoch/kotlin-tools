package net.rubygrapefruit.parse.text


internal class BufferingCharStream(bufferLen: Int = 64 * 1024) : AdvancingCharStream {
    private var tail = Buffer(null, 0, bufferLen)
    private var pos = 0
    override var finished: Boolean = false
        private set

    override val available: Int
        get() = tail.endIndex - pos

    override fun get(index: Int): Char {
        return tail.get(index + pos)
    }

    override fun get(start: Int, end: Int): String {
        return tail.get(start + pos, end + pos)
    }

    override fun posAt(index: Int): CharPosition {
        return tail.posAt(index + pos)
    }

    override fun contextAt(index: Int): CharFailureContext {
        return tail.contextAt(index + pos)
    }

    fun append(chars: CharArray) {
        tail = tail.append(chars, 0, chars.size)
    }

    override fun advance(count: Int) {
        pos += count
    }

    fun end() {
        finished = true
    }

    private class Buffer(private val previous: Buffer?, private val startIndex: Int, bufferLen: Int) {
        private var next: Buffer? = null
        private var writeIndex = 0
        private val content = CharArray(bufferLen)
        private var startPos: CharPosition? = null

        val endIndex: Int
            get() = startIndex + writeIndex

        fun get(index: Int): Char {
            return if (index < startIndex && previous != null) {
                previous.get(index)
            } else {
                content[index - startIndex]
            }
        }

        fun get(start: Int, end: Int): String {
            return if (start >= startIndex || previous == null) {
                val regionEnd = end - startIndex
                val regionStart = start - startIndex
                content.concatToString(regionStart, regionEnd)
            } else if (end <= startIndex) {
                previous.get(start, end)
            } else {
                val target = CharArray(end - start)
                previous.getInto(start, startIndex, target)
                content.copyInto(target, startIndex - start, 0, end - startIndex)
                target.concatToString()
            }
        }

        private fun getInto(start: Int, end: Int, target: CharArray) {
            if (start >= startIndex || previous == null) {
                content.copyInto(target, 0, start, end)
            } else {
                previous.getInto(start, startIndex, target)
                content.copyInto(target, startIndex - start, 0, end - startIndex)
            }
        }

        fun posAt(index: Int): CharPosition {
            return if (index >= startIndex || previous == null) {
                posAt(startPos(), index)
            } else {
                previous.posAt(index)
            }
        }

        fun contextAt(index: Int): CharFailureContext {
            return if (index >= startIndex || previous == null) {
                val startPos = startPos()
                var line = startPos.line
                var col = startPos.col
                var startLine = 0

                val contentIndex = index - startIndex
                for (i in 0 until contentIndex) {
                    if (content[i] == '\n') {
                        line++
                        col = 1
                        startLine = i + 1
                    } else {
                        col++
                    }
                }
                if (startLine == 0 && previous != null) {
                    TODO()
                }

                var endLine = -1
                for (i in contentIndex until writeIndex) {
                    if (content[i] == '\n') {
                        endLine = i
                        break
                    }
                }
                if (endLine < 0) {
                    if (next == null) {
                        endLine = writeIndex
                    } else {
                        TODO()
                    }
                }

                val pos = CharPosition(index, line, col)
                AdvancingCharStream.CharStreamContext(pos, content.concatToString(startLine, endLine))
            } else {
                previous.contextAt(index)
            }
        }

        private fun startPos(): CharPosition {
            val startPos = startPos
            return if (startPos != null) {
                startPos
            } else {
                val startPos = if (previous == null) {
                    CharPosition(0, 1, 1)
                } else {
                    previous.endPos()
                }
                this.startPos = startPos
                startPos
            }
        }

        private fun endPos(): CharPosition {
            return posAt(startPos(), writeIndex)
        }

        private fun posAt(startPos: CharPosition, index: Int): CharPosition {
            var line = startPos.line
            var col = startPos.col

            for (i in 0 until index - startIndex) {
                if (content[i] == '\n') {
                    line++
                    col = 1
                } else {
                    col++
                }
            }

            return CharPosition(index, line, col)
        }

        fun append(chars: CharArray, start: Int, end: Int): Buffer {
            if (end == start) {
                return this
            }

            val available = content.size - writeIndex
            val count = end - start
            return if (count <= available) {
                chars.copyInto(content, writeIndex, start, end)
                writeIndex += count
                this
            } else if (available == 0) {
                val next = appendBuffer()
                next.append(chars, start, end)
            } else {
                chars.copyInto(content, writeIndex, start, start + available)
                writeIndex += available
                val next = appendBuffer()
                next.append(chars, start + available, end)
            }
        }

        private fun appendBuffer(): Buffer {
            val next = Buffer(this, startIndex + writeIndex, content.size)
            this.next = next
            return next
        }
    }
}