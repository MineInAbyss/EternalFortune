package com.mineinabyss.eternalfortune.listeners

import com.mineinabyss.blocky.api.events.furniture.BlockyFurniturePlaceEvent
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
}
