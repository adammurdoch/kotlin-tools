package sample.calc

sealed class Expression {
    abstract fun evaluate(): Int
}

class Number(val value: Int) : Expression() {
    override fun evaluate(): Int {
        return value
    }
}

sealed class BinaryExpression : Expression() {
    abstract val left: Expression
    abstract val right: Expression
    abstract val operator: String
}

class Addition(override val left: Expression, override val right: Expression) : BinaryExpression() {
    override val operator: String
        get() = "+"

    override fun evaluate(): Int {
        return left.evaluate() + right.evaluate()
    }
}

class Subtraction(override val left: Expression, override val right: Expression) : BinaryExpression() {
    override val operator: String
        get() = "-"

    override fun evaluate(): Int {
        return left.evaluate() - right.evaluate()
    }
}
