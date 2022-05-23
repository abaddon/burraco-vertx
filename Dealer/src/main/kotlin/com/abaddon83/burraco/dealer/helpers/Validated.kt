package com.abaddon83.burraco.dealer.helpers

sealed class Validated<out A, out B> {
    data class Invalid<A>(val err: A): Validated<A, Nothing>()
    data class Valid<B>(val value: B): Validated<Nothing, B>()
}




