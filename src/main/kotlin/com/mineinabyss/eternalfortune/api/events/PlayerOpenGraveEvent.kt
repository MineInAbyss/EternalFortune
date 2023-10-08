package com.mineinabyss.eternalfortune.api.events

import com.mineinabyss.eternalfortune.components.Grave
import com.mineinabyss.idofront.entities.toOfflinePlayer
import org.bukkit.OfflinePlayer
import org.bukkit.entity.ItemDisplay
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.HandlerList
import org.bukkit.event.player.PlayerEvent

class PlayerOpenGraveEvent(
    player: Player,
    val entity: ItemDisplay,
    val grave: Grave,
) : PlayerEvent(player), Cancellable {

    val graveOwner: OfflinePlayer get() = grave.graveOwner.toOfflinePlayer()

    private var cancelled = false

    override fun isCancelled() = cancelled

    override fun setCancelled(cancel: Boolean) {
        cancelled = cancel
    }

    override fun getHandlers() = handlerList

    companion object {
        @JvmStatic
        val handlerList = HandlerList()
    }


}
