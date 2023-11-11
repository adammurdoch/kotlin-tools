package sample.calc

sealed class Expression {
    abstract fun evaluate(): Int
}

class Number(val value: Int): Expression() {
    override fun toString(): String {
        return value.toString()
    }

    override fun evaluate(): Int {
        return value
    }
}

class Addition(val left: Expression, val right: Expression): Expression() {
    override fun toString(): String {
        return "($left) + ($right)"
    }

    override fun evaluate(): Int {
        return left.evaluate() + right.evaluate()
    }
}
