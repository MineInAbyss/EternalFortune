package com.mineinabyss.eternalfortune.listeners

import com.github.shynixn.mccoroutine.bukkit.launch
import com.mineinabyss.eternalfortune.components.GraveOfflineNotice
import com.mineinabyss.eternalfortune.eternal
import com.mineinabyss.eternalfortune.extensions.EternalHelpers.spawnGrave
import com.mineinabyss.eternalfortune.extensions.isGrave
import com.mineinabyss.eternalfortune.extensions.playerGraves
import com.mineinabyss.eternalfortune.extensions.sendGraveTextDisplay
import com.mineinabyss.geary.papermc.tracking.entities.toGeary
import com.mineinabyss.idofront.messaging.error
import com.mineinabyss.idofront.textcomponents.miniMsg
import io.papermc.paper.event.packet.PlayerChunkLoadEvent
import kotlinx.coroutines.delay
import org.bukkit.GameRule
import org.bukkit.entity.ItemDisplay
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerJoinEvent
import kotlin.time.Duration.Companion.seconds

class PlayerListener : Listener {

    @EventHandler
    fun PlayerDeathEvent.onPlayerDeath() {
        when {
            (player.playerGraves?.graveUuids?.size ?: 0) >= eternal.config.maxGraveCount ->
                player.error(eternal.messages.HAS_GRAVE_ALREADY)
            drops.isEmpty() -> return // Only spawn grave if there were items and drop EXP like normal
            player.world.getGameRuleValue(GameRule.KEEP_INVENTORY) != true -> {
                // Clone list otherwise .clear() removes content somehow
                if (!player.spawnGrave(listOf(drops).flatten(), droppedExp)) return
                drops.clear()
                droppedExp = 0
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
    fun PlayerChunkLoadEvent.onChunkLoad() {
        chunk.entities.filterIsInstance<ItemDisplay>().filter { it.isGrave }.forEach { baseEntity ->
            player.sendGraveTextDisplay(baseEntity)
        }
    }
}
