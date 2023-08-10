package com.mineinabyss.eternalfortune

import com.mineinabyss.blocky.api.BlockyFurnitures.isBlockyFurniture
import com.mineinabyss.eternalfortune.extensions.EternalHelpers
import com.mineinabyss.eternalfortune.extensions.EternalMessages
import com.mineinabyss.eternalfortune.extensions.interactionHitboxIdMap
import com.mineinabyss.eternalfortune.extensions.removeGraveTextDisplay
import com.mineinabyss.eternalfortune.listeners.GraveListener
import com.mineinabyss.eternalfortune.listeners.PlayerListener
import com.mineinabyss.geary.addons.GearyPhase
import com.mineinabyss.geary.autoscan.autoscan
import com.mineinabyss.geary.modules.geary
import com.mineinabyss.idofront.config.config
import com.mineinabyss.idofront.di.DI
import com.mineinabyss.idofront.messaging.logError
import com.mineinabyss.idofront.platforms.Platforms
import com.mineinabyss.idofront.plugin.listeners
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class EternalFortune : JavaPlugin() {
    override fun onLoad() {
        Platforms.load(this, "mineinabyss")
    }

    override fun onEnable() {
        registerEternalContext()

        EternalCommands()

        listeners(GraveListener(), PlayerListener())

        geary {
            autoscan(classLoader, "com.mineinabyss.eternalfortune") {
                all()
            }
            on(GearyPhase.ENABLE) {
                if (!eternal.config.graveFurniture.isBlockyFurniture) {
                    logError("The graveFurniture config option must be a BlockyFurniture!")
                    Bukkit.getPluginManager().disablePlugin(this@EternalFortune)
                }
            }
        }
    }

    override fun onDisable() {
        interactionHitboxIdMap.values.forEach(::removeGraveTextDisplay)
    }

    fun registerEternalContext() {
        DI.remove<EternalContext>()
        DI.add<EternalContext>(object : EternalContext {
            override val plugin = this@EternalFortune
            override val config: EternalConfig by config("config") { fromPluginPath(loadDefault = true) }
            override val messages: EternalMessages by config("messages") { fromPluginPath(loadDefault = true) }
        })
    }
}
