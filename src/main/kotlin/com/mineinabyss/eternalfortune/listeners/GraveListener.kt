package com.mineinabyss.eternalfortune.listeners

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent
import com.mineinabyss.blocky.api.BlockyFurnitures
import com.mineinabyss.blocky.api.events.furniture.BlockyFurnitureBreakEvent
import com.mineinabyss.blocky.api.events.furniture.BlockyFurnitureInteractEvent
import com.mineinabyss.blocky.api.events.furniture.BlockyFurniturePlaceEvent
import com.mineinabyss.eternalfortune.api.events.PlayerOpenGraveEvent
import com.mineinabyss.eternalfortune.extensions.*
import com.mineinabyss.eternalfortune.extensions.EternalHelpers.graveInvMap
import com.mineinabyss.eternalfortune.extensions.EternalHelpers.openGraveInventory
import com.mineinabyss.geary.papermc.tracking.entities.events.GearyEntityAddToWorldEvent
import com.mineinabyss.idofront.entities.toOfflinePlayer
import org.bukkit.Sound
import org.bukkit.entity.EntityType
import org.bukkit.entity.ExperienceOrb
import org.bukkit.entity.ItemDisplay
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.inventory.EquipmentSlot

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
            hand != EquipmentSlot.HAND -> return // Only allow interaction with hand
            grave.isExpired() -> baseEntity.remove() // Mark for removal for EntityRemoveFromWorldEvent to handle
            else -> {
                // Call event to allow other plugins to handle who can bypass chest-protection
                // By default only the grave owner can open it, and event is called in
                // a cancelled state if a non-owner tries to open the grave
                val openEvent = PlayerOpenGraveEvent(player, baseEntity, grave)
                if (grave.isProtected() && grave.graveOwner != player.uniqueId && !player.hasPermission(EternalPermissions.BYPASS_OPEN_GRAVE))
                    openEvent.isCancelled = true
                if (openEvent.callEvent()) player.openGraveInventory(baseEntity)
                else player.playSound(baseEntity.location, Sound.BLOCK_CHEST_LOCKED, 1f, 1f)
            }
        }
    }

    @EventHandler
    fun BlockyFurnitureBreakEvent.onBreakGrave() {
        val grave = baseEntity.grave ?: return
        if (grave.isProtected() && !grave.isOwner(player)) isCancelled = true
    }

    @EventHandler
    fun GearyEntityAddToWorldEvent.onLoadExpiredGrave() {
        val itemDisplay = entity as? ItemDisplay ?: return
        val grave = itemDisplay.grave ?: return
        when {
            !grave.isExpired() -> itemDisplay.sendGraveTextToNearbyPlayers()
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
