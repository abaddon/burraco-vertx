package com.abaddon83.burraco.dealer.helpers

fun String.validUUID(): Boolean = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"
    .toRegex()
    .containsMatchIn(this)