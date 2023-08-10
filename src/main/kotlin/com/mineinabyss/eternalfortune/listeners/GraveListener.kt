package com.mineinabyss.eternalfortune.listeners

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent
import com.mineinabyss.blocky.api.BlockyFurnitures
import com.mineinabyss.blocky.api.events.furniture.BlockyFurnitureInteractEvent
import com.mineinabyss.blocky.api.events.furniture.BlockyFurniturePlaceEvent
import com.mineinabyss.eternalfortune.components.PlayerGraves
import com.mineinabyss.eternalfortune.extensions.*
import com.mineinabyss.eternalfortune.extensions.EternalHelpers.openGraveInventory
import com.mineinabyss.geary.papermc.datastore.decode
import com.mineinabyss.geary.papermc.datastore.encode
import com.mineinabyss.geary.papermc.datastore.remove
import com.mineinabyss.geary.papermc.tracking.entities.toGeary
import com.mineinabyss.idofront.entities.toOfflinePlayer
import com.mineinabyss.idofront.messaging.broadcast
import com.mineinabyss.idofront.messaging.logError
import org.bukkit.Bukkit
import org.bukkit.entity.ItemDisplay
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityDeathEvent

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
            grave.graveOwner == player.uniqueId -> player.openGraveInventory(baseEntity)
        }
    }

    @EventHandler
    fun EntityRemoveFromWorldEvent.onDeathGrave() {
        if (!entity.isDead) return
        val grave = (entity as? ItemDisplay)?.grave ?: return
        for (item in grave.graveContent) entity.world.dropItemNaturally(entity.location, item)

        val player = grave.graveOwner.toOfflinePlayer()
        when {
            player.isOnline -> player.player!!.toGeary().let { g ->
                g.get<PlayerGraves>()?.let { grave ->
                    g.setPersisting(
                        grave.copy(
                            graveUuids = grave.graveUuids - entity.uniqueId,
                            graveLocations = grave.graveLocations - entity.location
                        )
                    )
                }
            }

            else -> player.getOfflinePDC()?.let { pdc ->
                pdc.decode<PlayerGraves>()?.let { graves ->
                    pdc.encode(
                        graves.copy(
                            graveUuids = graves.graveUuids - entity.uniqueId,
                            graveLocations = graves.graveLocations - entity.location
                        )
                    )
                }
                if (!player.saveOfflinePDC(pdc)) logError("Failed to save grave data for ${player.name}")
            }
        }
        //TODO Find a way to unmark entity for removal so below code works
        if (!entity.isDead) BlockyFurnitures.removeFurniture(entity as ItemDisplay)
    }
}
