package com.mineinabyss.eternalfortune.listeners

import com.mineinabyss.blocky.api.events.furniture.BlockyFurnitureInteractEvent
import com.mineinabyss.blocky.api.events.furniture.BlockyFurniturePlaceEvent
import com.mineinabyss.eternalfortune.extensions.EternalHelpers.openGraveInventory
import com.mineinabyss.eternalfortune.extensions.grave
import com.mineinabyss.eternalfortune.extensions.isExpired
import com.mineinabyss.eternalfortune.extensions.isGrave
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

class GraveListener : Listener {
    // Prevent players from placing down graves normally if they somehow get the item
    @EventHandler(priority = EventPriority.HIGHEST)
    fun BlockyFurniturePlaceEvent.onPlaceGrave() {
        if (baseEntity.isGrave) isCancelled = true
    }

    @EventHandler
    fun BlockyFurnitureInteractEvent.onInteractGrave() {
        val grave = baseEntity.grave ?: return
        when {
            grave.isExpired() ->
                for (item in grave.graveContent) player.world.dropItemNaturally(baseEntity.location, item)
            //TODO Implement way for other plugins to configure this (MiA and Guilds)
            grave.graveOwner == player.uniqueId -> baseEntity.openGraveInventory(player)
        }
    }
}
