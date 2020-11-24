package com.abaddon83.utils.ddd.identity


abstract class Identity<T>(){
    abstract val id: T
    fun convertTo(): T {
        return id
    }
    override fun toString(): String = "${id.toString()}"

    abstract fun isEmpty(): Boolean

}