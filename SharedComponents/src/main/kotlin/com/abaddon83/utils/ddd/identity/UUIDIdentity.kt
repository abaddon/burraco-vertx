package com.abaddon83.utils.ddd.identity

import java.util.*

abstract class UUIDIdentity() : Identity<UUID>() {
    //override val id: UUID = id

    companion object{
        val emptyValue: UUID = UUID(0,0)
    }

    override fun isEmpty(): Boolean {
        return convertTo() == emptyValue
    }

}
