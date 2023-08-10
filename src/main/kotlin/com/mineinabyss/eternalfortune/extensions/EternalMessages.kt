package com.mineinabyss.eternalfortune.extensions

object EternalMessages {
    val NO_SPACE_FOR_GRAVE = "Could not find a suitable location to spawn a grave..."
    val HAS_GRAVE_ALREADY = "You already have a grave!"
    val GRAVE_EMPTIED = "Your grave has been emptied and removed"
    val FAILED_FILLING_GRAVE = "Failed to fill grave with items and experience"

    val GRAVE_TEXT = """
        <yellow>Grave of <gold><player>
        <gray>Expires in <red><expiration>
        <gray>Protected for <red><protection>
    """.trimIndent()
}
