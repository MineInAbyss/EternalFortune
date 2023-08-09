package com.mineinabyss.eternalfortune.components

import com.mineinabyss.idofront.serialization.LocationSerializer
import com.mineinabyss.idofront.serialization.UUIDSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.*
import org.bukkit.Location

@Serializable
@SerialName("eternalfortune:player_graves")
data class PlayerGraves(
    val graveUuids: List<@Serializable(UUIDSerializer::class) UUID>,
    val graveLocations: List<@Serializable(LocationSerializer::class) Location>
)
