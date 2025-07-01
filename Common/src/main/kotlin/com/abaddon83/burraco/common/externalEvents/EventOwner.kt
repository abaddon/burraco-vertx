package com.abaddon83.burraco.common.externalEvents

enum class EventOwner{
    PLAYER,
    GAME,
    DEALER;

    companion object {
        fun fromString(name: String): EventOwner = valueOf(name.uppercase())
    }
}