package com.abaddon83.burraco.common.serializations

import com.abaddon83.burraco.common.models.identities.GameIdentity
import com.abaddon83.burraco.common.models.identities.PlayerIdentity
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


//inline fun List<*>.toJson(): String {
//    if(this.isEmpty()){
//        return Json.encodeToString(listOf<String>())
//    }
//    return when(val firstElement =this.first()){
//        firstElement is Card -> Json.encodeToString(this as List<Card>)
//        firstElement is PlayerIdentity -> Json.encodeToString(this as List<PlayerIdentity>)
//        firstElement is Tris -> Json.encodeToString(this as List<Tris>)
//        firstElement is Scale -> Json.encodeToString(this as List<Scale>)
//        else -> throw NotImplementedError("toJson() method not supported for this class: ${this.javaClass.simpleName} ")
//    }
//}

inline fun GameIdentity.toJson(): String = Json.encodeToString(this)

inline fun PlayerIdentity.toJson(): String = Json.encodeToString(this)
