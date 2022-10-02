package net.rubygrapefruit.bytecode

internal class MethodDescriptor(
    val parameters: List<String>,
    val returnType: String
) {
    internal companion object {
        fun decode(descriptor: String): MethodDescriptor {
            var pos = 0
            if (descriptor[pos] != '(') {
                throw IllegalArgumentException("Expected '(' character in method descriptor: $descriptor")
            }
            pos++
            if (pos == descriptor.length) {
                throw IllegalArgumentException("Missing return type in method descriptor: $descriptor")
            }
            val parameterTypes = if (descriptor[pos] != ')') {
                val result = mutableListOf<String>()
                while (pos < descriptor.length && descriptor[pos] != ')') {
                    val type = fieldType(descriptor, pos, "method descriptor")
                    result.add(type.second)
                    pos = type.first
                }
                result
            } else {
                emptyList()
            }
            if (pos == descriptor.length || descriptor[pos] != ')') {
                throw IllegalArgumentException("Could not locate end of parameters in method descriptor: $descriptor")
            }
            pos++
            if (pos == descriptor.length) {
                throw IllegalArgumentException("Missing return type in method descriptor: $descriptor")
            }
            val returnType = returnType(descriptor, pos)
            if (returnType.first != descriptor.length) {
                throw IllegalArgumentException("Unexpected text after return type in method descriptor: $descriptor")
            }
            return MethodDescriptor(parameterTypes, returnType.second)
        }

        private fun returnType(descriptor: String, pos: Int): Pair<Int, String> {
            return if (descriptor[pos] == 'V') {
                return Pair(pos + 1, "void")
            } else {
                fieldType(descriptor, pos, "method descriptor")
            }
        }

        fun decodeFieldDescriptor(descriptor: String): String {
            val type = fieldType(descriptor, 0, "field descriptor")
            if (type.first != descriptor.length) {
                throw IllegalArgumentException("Unexpected text after type in field descriptor: $descriptor")
            }
            return type.second
        }

        private fun fieldType(descriptor: String, pos: Int, description: String): Pair<Int, String> {
            val ch = descriptor[pos]
            when (ch) {
                'Z' -> return Pair(pos + 1, "boolean")
                'C' -> return Pair(pos + 1, "char")
                'B' -> return Pair(pos + 1, "byte")
                'S' -> return Pair(pos + 1, "short")
                'I' -> return Pair(pos + 1, "int")
                'J' -> return Pair(pos + 1, "long")
                'F' -> return Pair(pos + 1, "float")
                'D' -> return Pair(pos + 1, "double")
                'L' -> {
                    val end = descriptor.indexOf(';', pos + 1)
                    if (end < 0) {
                        throw IllegalArgumentException("Could not find end of type in $description: $descriptor")
                    }
                    return Pair(end + 1, descriptor.substring(pos + 1, end).replace('/', '.'))
                }
                '[' -> {
                    val nested = fieldType(descriptor, pos + 1, description)
                    return Pair(nested.first, nested.second + "[]")
                }
                else -> throw UnsupportedOperationException("unrecognized type $ch in $description: $descriptor")
            }
        }
    }
}