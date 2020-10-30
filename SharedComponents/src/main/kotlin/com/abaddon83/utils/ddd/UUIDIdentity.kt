package com.abaddon83.utils.ddd

import java.util.*

abstract class UUIDIdentity(id: UUID) : Identity<UUID>(id) {


    companion object{
        val emptyValue: UUID = UUID(0,0)
    }

    override fun isEmpty(): Boolean {
        return convertTo() == UUIDIdentity.emptyValue
    }

}
