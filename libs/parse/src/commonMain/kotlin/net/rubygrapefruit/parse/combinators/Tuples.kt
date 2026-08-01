package net.rubygrapefruit.parse.combinators

internal class Tuple2<A, B>(val a: A, val b: B)

internal class Tuple3<A, B, C>(val a: A, val b: B, val c: C) {
    constructor(a: A, tail: Tuple2<B, C>) : this(a, tail.a, tail.b)
}

internal class Tuple4<A, B, C, D>(val a: A, val b: B, val c: C, val d: D) {
    constructor(a: A, tail: Tuple3<B,C,D>): this(a, tail.a, tail.b, tail.c)
}

internal class Tuple5<A, B, C, D, E>(val a: A, val b: B, val c: C, val d: D, val e: E) {
    constructor(a: A, tail: Tuple4<B, C, D, E>) : this(a, tail.a, tail.b, tail.c, tail.d)
}
