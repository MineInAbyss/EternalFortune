package com.mineinabyss.eternalfortune.listeners

import com.mineinabyss.eternalfortune.components.GraveOfflineNotice
import com.mineinabyss.eternalfortune.eternal
import com.mineinabyss.eternalfortune.extensions.EternalHelpers.spawnGrave
import com.mineinabyss.eternalfortune.extensions.EternalMessages
import com.mineinabyss.eternalfortune.extensions.playerGraves
import com.mineinabyss.geary.papermc.tracking.entities.toGeary
import com.mineinabyss.idofront.messaging.error
import com.mineinabyss.idofront.textcomponents.miniMsg
import org.bukkit.GameRule
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerJoinEvent

class PlayerListener : Listener {

    @EventHandler
    fun PlayerDeathEvent.onPlayerDeath() {
        when {
            (player.playerGraves?.graveUuids?.size ?: 0) >= eternal.config.maxGraveCount ->
                player.error(EternalMessages.HAS_GRAVE_ALREADY)
            player.world.getGameRuleValue(GameRule.KEEP_INVENTORY) != true -> player.spawnGrave()
        }
    }

    @EventHandler
    fun PlayerJoinEvent.onJoinWithNotice() {
        val notice = player.toGeary().get<GraveOfflineNotice>() ?: return
        player.sendMessage(notice.message.miniMsg())
    }
}
