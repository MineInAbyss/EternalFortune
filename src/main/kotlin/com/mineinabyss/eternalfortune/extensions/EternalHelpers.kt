package com.mineinabyss.eternalfortune.extensions

import com.comphenix.protocol.events.PacketContainer
import com.mineinabyss.blocky.api.BlockyFurnitures
import com.mineinabyss.blocky.api.BlockyFurnitures.blockyFurniture
import com.mineinabyss.blocky.helpers.FurnitureHelpers
import com.mineinabyss.blocky.helpers.GenericHelpers.toBlockCenterLocation
import com.mineinabyss.eternalfortune.components.Grave
import com.mineinabyss.eternalfortune.components.GraveOfflineNotice
import com.mineinabyss.eternalfortune.components.PlayerGraves
import com.mineinabyss.eternalfortune.eternal
import com.mineinabyss.geary.helpers.with
import com.mineinabyss.geary.papermc.datastore.decode
import com.mineinabyss.geary.papermc.datastore.encode
import com.mineinabyss.geary.papermc.tracking.entities.toGeary
import com.mineinabyss.geary.papermc.tracking.entities.toGearyOrNull
import com.mineinabyss.idofront.entities.toOfflinePlayer
import com.mineinabyss.idofront.messaging.error
import com.mineinabyss.idofront.messaging.logError
import com.mineinabyss.idofront.messaging.success
import com.mineinabyss.idofront.messaging.warn
import com.mineinabyss.idofront.nms.nbt.WrappedPDC
import com.mineinabyss.idofront.textcomponents.miniMsg
import com.mineinabyss.protocolburrito.dsl.sendTo
import dev.triumphteam.gui.guis.Gui
import dev.triumphteam.gui.guis.StorageGui
import it.unimi.dsi.fastutil.ints.IntList
import net.kyori.adventure.text.minimessage.tag.Tag
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtIo
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.phys.Vec3
import org.bukkit.*
import org.bukkit.craftbukkit.v1_20_R1.CraftServer
import org.bukkit.entity.ItemDisplay
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.io.File
import java.nio.file.Files
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime


object EternalHelpers {
    fun Player.spawnGrave(drops: List<ItemStack>, droppedExp: Int): Boolean {
        val graveLocation =
            location.findNearestSpawnableBlock() ?: run { this.error(EternalMessages.NO_SPACE_FOR_GRAVE); return false }
        val grave = BlockyFurnitures.placeFurniture(eternal.config.graveFurniture, graveLocation) ?: run {
            this.error(EternalMessages.NO_SPACE_FOR_GRAVE); return false
        }
        val expirationDate =
            LocalDateTime.now().plusSeconds(eternal.config.expirationTime.inWholeSeconds).toEpochSecond(ZoneOffset.UTC)
        val protectionDate =
            LocalDateTime.now().plusSeconds(eternal.config.protectionTime.inWholeSeconds).toEpochSecond(ZoneOffset.UTC)
        val playerGraves = this.toGeary().get<PlayerGraves>() ?: PlayerGraves(emptyList(), emptyList())

        this.toGeary().setPersisting(
            playerGraves.copy(
                graveUuids = playerGraves.graveUuids + grave.uniqueId,
                graveLocations = playerGraves.graveLocations + graveLocation
            )
        )
        grave.toGearyOrNull()?.setPersisting(Grave(uniqueId, drops, droppedExp, protectionDate, expirationDate))
            ?: run { this.error(EternalMessages.FAILED_FILLING_GRAVE); return false }
        this.success("Grave spawned at ${graveLocation.blockX} ${graveLocation.blockY} ${graveLocation.blockZ}!")
        Bukkit.getOnlinePlayers().forEach {
            it.sendGraveTextDisplay(grave)
        }
        return true
    }

    private fun Location.findNearestSpawnableBlock(): Location? {
        return when {
            FurnitureHelpers.hasEnoughSpace(eternal.config.graveFurniture.blockyFurniture!!, this, 0f) -> this
            else -> {
                val spawnRadius = eternal.config.spawnRadiusCheck
                //TODO Make this go from center and outwards
                for (x in -spawnRadius..spawnRadius) for (y in -spawnRadius..spawnRadius) for (z in -spawnRadius..spawnRadius)
                    if (FurnitureHelpers.hasEnoughSpace(
                            eternal.config.graveFurniture.blockyFurniture!!,
                            this.add(x.toDouble(), y.toDouble(), z.toDouble()),
                            0f
                        )
                    )
                        this.add(x.toDouble(), y.toDouble(), z.toDouble())
                null
            }
        }
    }

    internal val graveInvMap = mutableMapOf<UUID, StorageGui>()
    fun Player.openGraveInventory(baseEntity: ItemDisplay) {
        val graveInv = graveInvMap.getOrPut(baseEntity.uniqueId) { createGraveStorage(this, baseEntity) ?: return }
        graveInv.open(this)
    }

    private fun createGraveStorage(player: Player, baseEntity: ItemDisplay): StorageGui? {
        val grave = baseEntity.grave ?: return null
        return Gui.storage().title("Grave".miniMsg()).rows(3).create().let { gui ->
            gui.addItem(grave.graveContent)
            gui.disableItemPlace()
            gui.setCloseGuiAction { close ->
                when {
                    close.inventory.isEmpty -> {
                        val owner = grave.graveOwner.toOfflinePlayer()
                        when {
                            owner.isOnline -> owner.player!!.warn(EternalMessages.GRAVE_EMPTIED)
                            else -> {
                                val pdc = owner.getOfflinePDC()
                                    ?: return@setCloseGuiAction logError("Could not get PDC for ${owner.name}")
                                pdc.encode(GraveOfflineNotice(EternalMessages.GRAVE_EMPTIED))
                                owner.saveOfflinePDC(pdc)
                            }
                        }
                        owner.removeGraveFromPlayerGraves(baseEntity)
                        player.giveExp(grave.graveExp)
                        baseEntity.toGearyOrNull()?.setPersisting(grave.copy(graveContent = emptyList(), graveExp = 0))
                        BlockyFurnitures.removeFurniture(baseEntity)
                    }

                    else -> baseEntity.toGearyOrNull()
                        ?.setPersisting(grave.copy(graveContent = close.view.topInventory.storageContents.filterNotNull()))
                }
            }
            gui
        }
    }
}

val ItemDisplay.isGrave get() = toGearyOrNull()?.has<Grave>() == true
val ItemDisplay.grave get() = toGearyOrNull()?.get<Grave>()
fun Grave.isExpired() = expirationTime < currentTime()


fun currentTime() = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)

val Player.hasGraves
    get() = toGearyOrNull()?.get<PlayerGraves>()?.let { it.graveLocations.isNotEmpty() && it.graveUuids.isNotEmpty() }
        ?: false
val Player.playerGraves get() = toGearyOrNull()?.get<PlayerGraves>()
fun PlayerGraves.size() = graveUuids.size

/**
 * Gets the PlayerData from file for this UUID.
 */
internal fun UUID.getOfflinePlayerData(): CompoundTag? =
    (Bukkit.getServer() as CraftServer).handle.playerIo.getPlayerData(this.toString())

/**
 * Gets a copy of the WrappedPDC for this OfflinePlayer.
 * Care should be taken to ensure that the player is not online when this is called.
 */
fun OfflinePlayer.getOfflinePDC(): WrappedPDC? {
    if (isOnline) return null
    val baseTag = uniqueId.getOfflinePlayerData()?.getCompound("BukkitValues") ?: return null
    return WrappedPDC(baseTag)
}

/**
 * Saves the given WrappedPDC to the OfflinePlayer's PlayerData file.
 * Care should be taken to ensure that the player is not online when this is called.
 * @return true if successful, false otherwise.
 */
fun OfflinePlayer.saveOfflinePDC(pdc: WrappedPDC): Boolean {
    if (isOnline) return false
    val worldNBTStorage = (Bukkit.getServer() as CraftServer).server.playerDataStorage
    val tempFile = File(worldNBTStorage.playerDir, "$uniqueId.dat.tmp")
    val playerFile = File(worldNBTStorage.playerDir, "$uniqueId.dat")

    val mainPDc = uniqueId.getOfflinePlayerData() ?: return false
    mainPDc.put("BukkitValues", pdc.compoundTag) ?: return false
    runCatching {
        Files.newOutputStream(tempFile.toPath()).use { outStream ->
            NbtIo.writeCompressed(mainPDc, outStream)
            if (playerFile.exists() && !playerFile.delete()) logError("Failed to delete player file $uniqueId")
            if (!tempFile.renameTo(playerFile)) logError("Failed to rename player file $uniqueId")
        }
    }.onFailure {
        logError("Failed to save player file $uniqueId")
        it.printStackTrace()
        return false
    }
    return true
}


fun Location.ensureWorldIsLoaded() {
    if (Bukkit.isTickingWorlds()) ensureWorldIsLoaded()
    else if (!isWorldLoaded) Bukkit.createWorld(WorldCreator.ofKey(world.key))

}

val interactionHitboxIdMap = mutableMapOf<UUID, Int>()

@OptIn(ExperimentalTime::class)
fun Player.sendGraveTextDisplay(baseEntity: ItemDisplay) {
    val entityId = interactionHitboxIdMap.computeIfAbsent(baseEntity.uniqueId) { Entity.nextEntityId() }
    val loc = baseEntity.location.toBlockCenterLocation().add(0.0, eternal.config.textDisplayOffset, 0.0)
    val textDisplayPacket = ClientboundAddEntityPacket(
        entityId, UUID.randomUUID(),
        loc.x, loc.y, loc.z, loc.pitch, loc.yaw,
        EntityType.TEXT_DISPLAY, 0, Vec3.ZERO, 0.0
    )

    PacketContainer.fromPacket(textDisplayPacket).sendTo(this)
    fun formatDuration(duration: Duration): String {
        val hours = duration.inWholeHours
        val minutes = duration.minus(hours.hours).inWholeMinutes
        val seconds = duration.minus(hours.hours).minus(minutes.minutes).inWholeSeconds
        return String.format("%02dh:%02dm:%02ds", hours, minutes, seconds)
    }
    fun convertTime(duration: Long) = formatDuration(Duration.convert((duration - currentTime()).toDouble(), DurationUnit.SECONDS, DurationUnit.SECONDS).seconds)
    val tagResolver = TagResolver.resolver(
        TagResolver.resolver("player", Tag.inserting(baseEntity.grave!!.graveOwner.toOfflinePlayer().name.toString().miniMsg())),
        TagResolver.resolver("protection", Tag.inserting(convertTime(baseEntity.grave!!.protectionTime).miniMsg())),
        TagResolver.resolver("expiration", Tag.inserting(convertTime(baseEntity.grave!!.expirationTime).miniMsg())),
    )
    val text = LegacyComponentSerializer.legacySection().serialize(EternalMessages.GRAVE_TEXT.trimIndent().miniMsg(tagResolver))

    // Set flags using bitwise operations
    var bitmask = 0
    bitmask = bitmask or 0x01 // Set bit 0 (Has shadow)
    bitmask = bitmask or (0 and 0x0F shl 3) // Set alignment to CENTER (0)

    PacketContainer.fromPacket(
        ClientboundSetEntityDataPacket(
            entityId, listOf(
                SynchedEntityData.DataValue(14, EntityDataSerializers.BYTE, 1), // Billboard
                SynchedEntityData.DataValue(22, EntityDataSerializers.COMPONENT, Component.literal(text)),
                SynchedEntityData.DataValue(24, EntityDataSerializers.INT, Color.fromARGB(0,0,0,0).asARGB()), // Transparent background
                SynchedEntityData.DataValue(26, EntityDataSerializers.BYTE, bitmask.toByte())
            )
        )
    ).sendTo(this@sendGraveTextDisplay)
}

fun removeGraveTextDisplay(baseEntity: ItemDisplay) {
    val entityId = interactionHitboxIdMap.remove(baseEntity.uniqueId) ?: return
    val destroyPacket = ClientboundRemoveEntitiesPacket(IntList.of(entityId))
    Bukkit.getOnlinePlayers().forEach { PacketContainer.fromPacket(destroyPacket).sendTo(it) }
}

fun removeGraveTextDisplay(entityId: Int) {
    interactionHitboxIdMap.entries.removeIf { it.value == entityId }
    val destroyPacket = ClientboundRemoveEntitiesPacket(IntList.of(entityId))
    Bukkit.getOnlinePlayers().forEach { PacketContainer.fromPacket(destroyPacket).sendTo(it) }
}

fun OfflinePlayer.removeGraveFromPlayerGraves(baseEntity: ItemDisplay) {
    removeGraveFromPlayerGraves(baseEntity.uniqueId, baseEntity.location)
}

fun OfflinePlayer.removeGraveFromPlayerGraves(uuid: UUID, loc: Location) {
    when {
        isOnline -> {
            player!!.toGeary().with { playerGraves: PlayerGraves ->
                player!!.toGeary().setPersisting(
                    playerGraves.copy(
                        graveUuids = playerGraves.graveUuids - uuid,
                        graveLocations = playerGraves.graveLocations - loc
                    )
                )
            }
        }

        else -> {
            getOfflinePDC()?.let { pdc ->
                pdc.decode<PlayerGraves>()?.let {
                    pdc.encode(
                        it.copy(
                            graveUuids = it.graveUuids - uuid,
                            graveLocations = it.graveLocations - loc
                        )
                    )
                }
                saveOfflinePDC(pdc)
            }
        }
    }
}
