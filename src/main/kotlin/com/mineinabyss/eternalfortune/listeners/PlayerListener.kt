package com.mineinabyss.eternalfortune.listeners

import com.github.shynixn.mccoroutine.bukkit.launch
import com.mineinabyss.eternalfortune.api.events.PlayerCreateGraveEvent
import com.mineinabyss.eternalfortune.components.GraveOfflineNotice
import com.mineinabyss.eternalfortune.eternal
import com.mineinabyss.eternalfortune.extensions.EternalHelpers.spawnGrave
import com.mineinabyss.eternalfortune.extensions.isGrave
import com.mineinabyss.eternalfortune.extensions.playerGraves
import com.mineinabyss.eternalfortune.extensions.sendGraveTextToNearbyPlayers
import com.mineinabyss.geary.papermc.tracking.entities.events.GearyEntityAddToWorldEvent
import com.mineinabyss.geary.papermc.tracking.entities.toGeary
import com.mineinabyss.idofront.messaging.error
import com.mineinabyss.idofront.textcomponents.miniMsg
import kotlinx.coroutines.delay
import org.bukkit.entity.ItemDisplay
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerJoinEvent
import kotlin.time.Duration.Companion.seconds

class PlayerListener : Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    fun PlayerDeathEvent.onPlayerDeath() {
        when {
            (player.playerGraves?.graveUuids?.size ?: 0) >= eternal.config.maxGraveCount ->
                player.error(eternal.messages.HAS_GRAVE_ALREADY)
            drops.isEmpty() -> return // Only spawn grave if there were items and drop EXP like normal
            eternal.config.ignoreKeepInv || !keepInventory -> {
                val graveEvent = PlayerCreateGraveEvent(player, drops, keepLevel)
                val grave = player.spawnGrave(listOf(drops).flatten(), droppedExp) ?: return
                if (graveEvent.callEvent()) {
                    keepLevel = eternal.config.keepExp
                    drops.clear()
                    if (keepLevel) droppedExp = 0
                } else grave.remove()
            }
        }
    }

    @EventHandler
    fun PlayerJoinEvent.onJoinWithNotice() {
        eternal.plugin.launch {
            delay(1.seconds)
            val notice = player.toGeary().get<GraveOfflineNotice>() ?: return@launch
            for (message in notice.messages) player.sendMessage(message.miniMsg())
            player.toGeary().remove<GraveOfflineNotice>()
        }
    }

    @EventHandler
    fun GearyEntityAddToWorldEvent.onGraveLoad() {
        val itemDisplay = entity as? ItemDisplay ?: return
        if (itemDisplay.isGrave) itemDisplay.sendGraveTextToNearbyPlayers()
    }
}
