package com.mineinabyss.eternalfortune.listeners

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent
import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent
import com.mineinabyss.blocky.api.BlockyFurnitures
import com.mineinabyss.blocky.api.events.furniture.BlockyFurnitureBreakEvent
import com.mineinabyss.blocky.api.events.furniture.BlockyFurnitureInteractEvent
import com.mineinabyss.blocky.api.events.furniture.BlockyFurniturePlaceEvent
import com.mineinabyss.eternalfortune.extensions.*
import com.mineinabyss.eternalfortune.extensions.EternalHelpers.graveInvMap
import com.mineinabyss.eternalfortune.extensions.EternalHelpers.openGraveInventory
import com.mineinabyss.idofront.entities.toOfflinePlayer
import org.bukkit.entity.EntityType
import org.bukkit.entity.ExperienceOrb
import org.bukkit.entity.ItemDisplay
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
            grave.isExpired() -> baseEntity.remove() // Mark for removal for EntityRemoveFromWorldEvent to handle
            //TODO Implement way for other plugins to configure this (MiA and Guilds)
            !grave.isProtected() || grave.graveOwner == player.uniqueId -> player.openGraveInventory(baseEntity)
        }
    }

    @EventHandler
    fun BlockyFurnitureBreakEvent.onBreakGrave() {
        val grave = baseEntity.grave ?: return
        if (grave.isProtected() && grave.graveOwner == player.uniqueId) isCancelled = true
    }

    @EventHandler
    fun EntityAddToWorldEvent.onLoadExpiredGrave() {
        val itemDisplay = entity as? ItemDisplay ?: return
        val grave = itemDisplay.grave ?: return
        when {
            !grave.isExpired() -> itemDisplay.world.getNearbyPlayers(itemDisplay.location, 16.0).forEach { it.sendGraveTextDisplay(itemDisplay) }
            else -> itemDisplay.remove()
        }
    }

    /**
     * When a grave is marked for removal, either via commands or being broken in any way
     * The below listener will handle completely removing it and all assosiacted playerdata
     * Since /kill commands wouldn't trigger BlockyFurnitureBreakEvent then the main logic should be done here
     */
    @EventHandler
    fun EntityRemoveFromWorldEvent.onDeathGrave() {
        if (!entity.isDead) return // If furniture isn't being removed, ignore
        val itemDisplay = entity as? ItemDisplay ?: return
        val gui = graveInvMap[itemDisplay.uniqueId]
        val grave = itemDisplay.grave ?: return
        val content = gui?.inventory?.contents?.toList()?.filterNotNull() ?: grave.graveContent

        for (viewer in gui?.inventory?.viewers ?: emptyList()) gui?.close(viewer)
        for (item in content) entity.world.dropItemNaturally(entity.location, item)
        if (grave.graveExp > 0) (entity.world.spawnEntity(entity.location, EntityType.EXPERIENCE_ORB) as? ExperienceOrb)?.experience = grave.graveExp
        graveInvMap.remove(entity.uniqueId)

        grave.graveOwner.toOfflinePlayer().removeGraveFromPlayerGraves(itemDisplay)
        removeGraveTextDisplay(itemDisplay)
        BlockyFurnitures.removeFurniture(itemDisplay)
    }
}
