package com.abaddon83.burraco.common.serializations

import com.abaddon83.burraco.common.models.identities.GameIdentity
import com.abaddon83.burraco.common.models.identities.PlayerIdentity
import com.abaddon83.burraco.common.models.valueObjects.Card
import com.abaddon83.burraco.common.models.valueObjects.Scale
import com.abaddon83.burraco.common.models.valueObjects.Tris
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


inline fun kotlin.collections.List<*>.toJson(): String {
    if(this.isEmpty())  return ""

    return when(this.first()){
        is Card -> Json.encodeToString(this as List<Card>)
        is PlayerIdentity -> Json.encodeToString(this as List<PlayerIdentity>)
        is Tris -> Json.encodeToString(this as List<Tris>)
        is Scale -> Json.encodeToString(this as List<Scale>)
        else -> throw NotImplementedError("toJson() method not supported for this class: ${this.javaClass.simpleName} ")
    }
}

inline fun GameIdentity.toJson(): String = Json.encodeToString(this)

inline fun PlayerIdentity.toJson(): String = Json.encodeToString(this)
