package com.mineinabyss.eternalfortune.listeners

import com.comphenix.protocol.events.PacketContainer
import com.mineinabyss.blocky.api.BlockyFurnitures.isBlockyFurniture
import com.mineinabyss.blocky.helpers.GenericHelpers.toBlockCenterLocation
import com.mineinabyss.eternalfortune.components.GraveOfflineNotice
import com.mineinabyss.eternalfortune.eternal
import com.mineinabyss.eternalfortune.extensions.*
import com.mineinabyss.eternalfortune.extensions.EternalHelpers.spawnGrave
import com.mineinabyss.geary.helpers.entity
import com.mineinabyss.geary.papermc.tracking.entities.toGeary
import com.mineinabyss.idofront.entities.toOfflinePlayer
import com.mineinabyss.idofront.messaging.error
import com.mineinabyss.idofront.textcomponents.miniMsg
import com.mineinabyss.protocolburrito.dsl.sendTo
import io.papermc.paper.event.packet.PlayerChunkLoadEvent
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.phys.Vec3
import org.bukkit.GameRule
import org.bukkit.entity.ItemDisplay
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerJoinEvent
import java.util.*
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime

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
        val notice = player.toGeary().get<GraveOfflineNotice>() ?: return
        player.sendMessage(notice.message.miniMsg())
    }

    @EventHandler
    fun PlayerChunkLoadEvent.onChunkLoad() {
        chunk.entities.filterIsInstance<ItemDisplay>().filter { it.isGrave }.forEach { baseEntity ->
            player.sendGraveTextDisplay(baseEntity)
        }
    }
}
