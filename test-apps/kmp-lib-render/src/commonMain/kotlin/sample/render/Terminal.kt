package sample.render

expect fun terminal(): Terminal

open class Terminal {
    open fun literal(value: Any) {
        print(value)
    }

    open fun operator(value: Any) {
        print(value)
    }

    fun whitespace(value: String) {
        print(value)
    }

    data object Plain : Terminal()
}

