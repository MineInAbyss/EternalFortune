package com.mineinabyss.eternalfortune

import com.mineinabyss.idofront.slimjar.LibraryLoaderInjector
import org.bukkit.plugin.java.JavaPlugin

val eternalFortunePlugin: EternalFortune by lazy { JavaPlugin.getPlugin(EternalFortune::class.java) }

class EternalFortune : JavaPlugin() {
    override fun onEnable() {
        LibraryLoaderInjector.inject(this)
        saveDefaultConfig()
    }
}