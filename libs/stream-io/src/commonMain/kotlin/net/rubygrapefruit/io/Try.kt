package net.rubygrapefruit.io

sealed interface Try<out T, out F : IOException> : Result<T, F> {
    companion object {
        fun <T, F : IOException> succeeded(value: T) = TrySuccess<T, F>(value)
        fun <T, F : IOException> failed(failure: F) = TryFailure<T, F>(failure)
    }
}

class TrySuccess<out T, out F : IOException>(value: T) : Try<T, F>, DefaultSuccess<T, F>(value)

class TryFailure<out T, out F : IOException>(failure: F) : Try<T, F>, DefaultFailure<T, F>(failure)
