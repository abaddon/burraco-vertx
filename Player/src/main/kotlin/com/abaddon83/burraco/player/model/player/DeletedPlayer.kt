package com.abaddon83.burraco.player.model.player

import com.abaddon83.burraco.common.models.GameIdentity
import io.github.abaddon.kcqrs.core.IIdentity

class DeletedPlayer(
    override val id: IIdentity,
    override val version: Long,
    override val gameIdentity: GameIdentity,
    override val user: String
) : Player() {
}