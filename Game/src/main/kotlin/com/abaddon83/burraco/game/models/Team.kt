package com.abaddon83.burraco.game.models

import com.abaddon83.burraco.game.models.player.PlayerIdentity

data class Team(val members: List<PlayerIdentity>, val playerDeckPickedUp: Boolean= false)
