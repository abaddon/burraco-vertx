package com.abaddon83.burraco.readModel.projections

import com.abaddon83.burraco.common.models.identities.GameIdentity
import com.abaddon83.burraco.common.models.identities.PlayerIdentity
import com.abaddon83.utils.ddd.readModel.ProjectionKey

data class BurracoGameKey(
    val gameIdentity: GameIdentity
): ProjectionKey(){

}