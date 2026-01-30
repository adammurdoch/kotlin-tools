package sample.calc

sealed class Expression {
    abstract fun evaluate(): Number
}

class Number(val value: Int) : Expression() {
    override fun toString(): String {
        return value.toString()
    }

    override fun evaluate(): Number {
        return this
    }
}

class Addition(val left: Expression, val right: Expression) : Expression() {
    override fun toString(): String {
        return "($left) + ($right)"
    }

    override fun evaluate(): Number {
        return Number(left.evaluate().value + right.evaluate().value)
    }
}
