package com.mineinabyss.eternalfortune

import com.mineinabyss.eternalfortune.components.PlayerGraves
import com.mineinabyss.eternalfortune.extensions.*
import com.mineinabyss.eternalfortune.extensions.EternalHelpers.spawnGrave
import com.mineinabyss.geary.papermc.datastore.decode
import com.mineinabyss.idofront.commands.arguments.offlinePlayerArg
import com.mineinabyss.idofront.commands.arguments.playerArg
import com.mineinabyss.idofront.commands.execution.IdofrontCommandExecutor
import com.mineinabyss.idofront.commands.extensions.actions.playerAction
import com.mineinabyss.idofront.messaging.error
import com.mineinabyss.idofront.messaging.success
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.ItemDisplay
import org.bukkit.entity.Player

class EternalCommands : IdofrontCommandExecutor(), TabCompleter {

    override val commands = commands(eternal.plugin) {
        command("eternalfortune", "eternal", "ef") {
            "reload" {
                action {
                    eternal.plugin.registerEternalContext()
                    sender.success("EternalFortune configs have been reloaded!")
                }
            }
            "graves" {
                "place" {
                    val player: Player by playerArg()
                    action {
                        player.spawnGrave(player.inventory.storageContents.toList().filterNotNull(), 0)
                    }
                }
                "text" {
                    playerAction {
                        player.getNearbyEntities(10.0, 10.0, 10.0).filterIsInstance<ItemDisplay>().filter { it.isGrave }.forEach {
                            player.sendGraveTextDisplay(it)
                        }
                    }
                }
                "remove" {
                    val player: OfflinePlayer by offlinePlayerArg()
                    action {
                        val playerGraves = when {
                            player.isOnline -> player.player!!.playerGraves
                            else -> player.getOfflinePDC()?.decode<PlayerGraves>()
                        } ?: return@action sender.error("Player has no graves")

                        playerGraves.graveUuids.zip(playerGraves.graveLocations).toSet().forEach { (uuid, loc) ->
                            loc.ensureWorldIsLoaded()
                            loc.world.getChunkAtAsync(loc).thenAccept { c ->
                                val graveEntity = c.entities.find { it.uniqueId == uuid } as? ItemDisplay ?: return@thenAccept sender.error("Could not find grave entity")
                                if (graveEntity.grave?.graveContent?.isNotEmpty() == true)
                                    for (item in graveEntity.grave!!.graveContent)
                                        graveEntity.world.dropItemNaturally(graveEntity.location, item)
                                graveEntity.remove()

                                // Remove the grave from the player's data
                                player.removeGraveFromPlayerGraves(uuid, loc)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): List<String> {
        if (command.label != "eternal") return emptyList()
        when (args.size) {
            1 -> listOf("graves").filter { it.startsWith(args[0]) }
            2 -> when (args[0]) {
                "graves" -> listOf("place", "remove", "text").filter { it.startsWith(args[1]) }
            }
            3 -> when (args[1]) {
                "remove" -> Bukkit.getOnlinePlayers().map { it.name }.filter { it.startsWith(args[2]) }
            }
            else -> emptyList()
        }
        return emptyList()
    }
}
