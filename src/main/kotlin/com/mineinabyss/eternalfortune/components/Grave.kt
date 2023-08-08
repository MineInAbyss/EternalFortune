package com.mineinabyss.eternalfortune.components

import com.mineinabyss.idofront.serialization.DurationSerializer
import com.mineinabyss.idofront.serialization.ItemStackSerializer
import com.mineinabyss.idofront.serialization.UUIDSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.inventory.ItemStack
import java.util.UUID
import kotlin.time.Duration

@Serializable
@SerialName("eternalfortune:grave")
class Grave(
    val expirationTime: @Serializable(DurationSerializer::class) Duration,
)
