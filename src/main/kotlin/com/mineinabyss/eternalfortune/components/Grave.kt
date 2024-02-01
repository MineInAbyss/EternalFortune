package com.mineinabyss.eternalfortune.components

import com.mineinabyss.eternalfortune.extensions.currentTime
import com.mineinabyss.idofront.serialization.DurationSerializer
import com.mineinabyss.idofront.serialization.ItemStackSerializer
import com.mineinabyss.idofront.serialization.UUIDSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.UUID
import kotlin.time.Duration

@Serializable
@SerialName("eternalfortune:grave")
data class Grave(
    val graveOwner: @Serializable(UUIDSerializer::class) UUID,
    val graveContent: List<@Serializable(ItemStackSerializer::class) ItemStack>,
    val graveExp: Int = 0,
    val expirationTime: Long,
    val protectionTime: Long,
) {
    fun isExpired() = expirationTime < currentTime()
    fun isProtected() = protectionTime > currentTime()
    fun isOwner(player: Player) = graveOwner == player.uniqueId
}
