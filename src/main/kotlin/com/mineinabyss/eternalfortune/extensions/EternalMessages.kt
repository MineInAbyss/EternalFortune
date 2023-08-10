package com.mineinabyss.eternalfortune.extensions

import kotlinx.serialization.Serializable

@Serializable
data class EternalMessages(
    val NO_SPACE_FOR_GRAVE: String = "Could not find a suitable location to spawn a grave...",
    val HAS_GRAVE_ALREADY: String = "You already have a grave!",
    val GRAVE_EMPTIED: String = "Your grave has been emptied and removed",
    val FAILED_FILLING_GRAVE: String = "Failed to fill grave with items and experience",

    val GRAVE_TEXT: String = """
        <yellow>Grave of <gold><player>
        <gray>Expires in <red><expiration>
        <gray>Protected for <red><protection>
    """.trimIndent(),
)
