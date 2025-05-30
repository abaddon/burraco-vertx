package com.abaddon83.burraco.common.helpers

inline fun <T, R> Result<T>.flatMap(transform: (T) -> Result<R>): Result<R> {
    return when {
        isSuccess -> transform(getOrThrow())
        else -> Result.failure(exceptionOrNull()!!)
    }
}