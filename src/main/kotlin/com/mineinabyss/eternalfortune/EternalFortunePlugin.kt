package com.mineinabyss.eternalfortune

import com.mineinabyss.eternalfortune.data.Graves
import com.mineinabyss.eternalfortune.data.MessageQueue
import com.mineinabyss.eternalfortune.data.Players
import com.mineinabyss.idofront.platforms.IdofrontPlatforms
import com.mineinabyss.idofront.plugin.getService
import com.mineinabyss.idofront.plugin.registerService
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction

val eternalFortunePlugin: EternalFortune by lazy { JavaPlugin.getPlugin(EternalFortune::class.java) }

interface EternalFortuneContext {
    companion object : EternalFortuneContext by getService()

    val db: Database
}

class EternalFortune : JavaPlugin() {
    override fun onLoad() {
        IdofrontPlatforms.load(this, "mineinabyss")
    }

    override fun onEnable() {
        saveDefaultConfig()
        EternalFortuneConfig.load()
        registerService<EternalFortuneContext>(object : EternalFortuneContext {
            override val db = Database.connect("jdbc:sqlite:" + dataFolder.path + "/data.db", "org.sqlite.JDBC")
        })

        transaction(EternalFortuneContext.db) {
            addLogger(StdOutSqlLogger)

            SchemaUtils.createMissingTablesAndColumns(Graves, Players, MessageQueue)
        }

        EternalFortuneCommandExecutor
    }
}