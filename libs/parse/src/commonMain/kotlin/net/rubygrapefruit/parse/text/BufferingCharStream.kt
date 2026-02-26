package net.rubygrapefruit.parse.text


internal class BufferingCharStream(bufferLen: Int = 64 * 1024) : AdvancingCharStream {
    private var tail = Buffer(null, 0, 1, 1, bufferLen)
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

    fun append(chars: CharArray, offset: Int, count: Int) {
        tail = tail.append(chars, offset, offset + count)
    }

    override fun advance(count: Int) {
        pos += count
    }

    fun end() {
        finished = true
    }

    private class Buffer(
        private val previous: Buffer?,
        private val startIndex: Int,
        private val startLine: Int,
        private val startCol: Int,
        bufferLen: Int
    ) {
        private var next: Buffer? = null
        private val content = CharArray(bufferLen)
        private var writeIndex = 0
        private var endLine = startLine
        private var endCol = startCol

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
                posAtForSelf(index)
            } else {
                previous.posAt(index)
            }
        }

        fun contextAt(index: Int): CharFailureContext {
            return if (index >= startIndex || previous == null) {
                var line = startLine
                var col = startCol
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
                val builder = ContextBuilder(this)
                if (startLine == 0 && previous != null) {
                    previous.findStartLastLine(builder)
                } else {
                    builder.startLine = startIndex + startLine
                }

                var endLine = -1
                for (i in contentIndex until writeIndex) {
                    if (content[i] == '\n') {
                        endLine = i
                        break
                    }
                }
                if (endLine < 0) {
                    val next = next
                    if (next == null) {
                        builder.endLine = startIndex + writeIndex
                    } else {
                        next.findEndFirstLine(builder)
                    }
                } else {
                    builder.endLine = startIndex + endLine
                }

                val pos = CharPosition(index, line, col)
                AdvancingCharStream.CharStreamContext(pos, builder.endBuffer.get(builder.startLine, builder.endLine))
            } else {
                previous.contextAt(index)
            }
        }

        private fun findStartLastLine(builder: ContextBuilder) {
            for (i in writeIndex downTo 1) {
                if (content[i - 1] == '\n') {
                    builder.startLine = startIndex + i
                    builder.startBuffer = this
                    return
                }
            }
            if (previous != null) {
                previous.findStartLastLine(builder)
            } else {
                builder.startLine = 0
                builder.startBuffer = this
            }
        }

        private fun findEndFirstLine(builder: ContextBuilder) {
            for (i in 0 until writeIndex) {
                if (content[i] == '\n') {
                    builder.endLine = startIndex + i
                    builder.endBuffer = this
                    return
                }
            }
            val next = next
            if (next != null) {
                next.findEndFirstLine(builder)
            } else {
                builder.endLine = startIndex + writeIndex
                builder.endBuffer = this
            }
        }

        private fun posAtForSelf(index: Int): CharPosition {
            var line = startLine
            var col = startCol

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
                appendToContent(chars, start, end, count)
                this
            } else if (available == 0) {
                val next = appendBuffer()
                next.append(chars, start, end)
            } else {
                val endSlice = start + available
                appendToContent(chars, start, endSlice, available)
                val next = appendBuffer()
                next.append(chars, endSlice, end)
            }
        }

        private fun appendToContent(chars: CharArray, start: Int, end: Int, count: Int) {
            chars.copyInto(content, writeIndex, start, end)
            val endContent = writeIndex + count
            for (index in writeIndex until endContent) {
                if (content[index] == '\n') {
                    endLine++
                    endCol = 1
                } else {
                    endCol++
                }
            }
            writeIndex = endContent
        }

        private fun appendBuffer(): Buffer {
            val next = Buffer(this, startIndex + writeIndex, endLine, endCol, content.size)
            this.next = next
            return next
        }
    }

    private class ContextBuilder(
        var startBuffer: Buffer
    ) {
        var startLine: Int = 0
        var endBuffer = startBuffer
        var endLine: Int = 0
    }
}