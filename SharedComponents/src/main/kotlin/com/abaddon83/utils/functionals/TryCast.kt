package com.abaddon83.utils.functionals

import kotlin.reflect.typeOf

@OptIn(ExperimentalStdlibApi::class)
inline fun <reified T> Any?.tryCast():Boolean {
    println("TEST: this == T ? ${this?.javaClass?.simpleName} == ${
        typeOf<T>().javaClass.simpleName}")
    if (this is T) {
        return true
    }
    return false
}

inline fun <reified T> Any?.checkType(obj: List<*>, contract: T):Boolean = obj is T

inline fun <reified T> Any?.tryCastAndExecute(block: T.() -> Unit) {
    if (this is T) {
        block()
    }
}