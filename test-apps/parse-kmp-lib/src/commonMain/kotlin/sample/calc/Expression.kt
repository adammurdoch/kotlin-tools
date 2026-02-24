package sample.calc

sealed class Expression {
    abstract fun evaluate(): Number
}

data class Number(val value: Int) : Expression() {
    override fun evaluate(): Number {
        return this
    }
}

sealed class BinaryExpression : Expression() {
    abstract val left: Expression
    abstract val right: Expression
    abstract val operator: String
}

data class Addition(override val left: Expression, override val right: Expression) : BinaryExpression() {
    override val operator: String
        get() = "+"

    override fun evaluate(): Number {
        return Number(left.evaluate().value + right.evaluate().value)
    }
}

data class Subtraction(override val left: Expression, override val right: Expression) : BinaryExpression() {
    override val operator: String
        get() = "-"

    override fun evaluate(): Number {
        return Number(left.evaluate().value - right.evaluate().value)
    }
}
