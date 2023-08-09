package com.mineinabyss.eternalfortune.listeners

import com.mineinabyss.eternalfortune.eternal
import com.mineinabyss.eternalfortune.extensions.EternalHelpers.spawnGrave
import com.mineinabyss.eternalfortune.extensions.EternalMessages
import com.mineinabyss.eternalfortune.extensions.playerGraves
import com.mineinabyss.idofront.messaging.error
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent

class PlayerListener : Listener {

    @EventHandler
    fun PlayerDeathEvent.onPlayerDeath() {
        when {
            (player.playerGraves?.graveUuids?.size ?: 0) >= eternal.config.maxGraveCount ->
                player.error(EternalMessages.HAS_GRAVE_ALREADY)
            else -> {
                player.spawnGrave()
            }
        }
    }
}
