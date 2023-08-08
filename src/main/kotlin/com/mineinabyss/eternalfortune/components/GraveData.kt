package com.mineinabyss.eternalfortune.components

import com.mineinabyss.idofront.serialization.ItemStackSerializer
import com.mineinabyss.idofront.serialization.UUIDSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.inventory.ItemStack
import java.util.*

@Serializable
@SerialName("eternalfortune:grave_data")
data class GraveData(
    val graveOwner: @Serializable(UUIDSerializer::class) UUID? = null,
    val graveContent: List<@Serializable(ItemStackSerializer::class) ItemStack> = emptyList(),
)
