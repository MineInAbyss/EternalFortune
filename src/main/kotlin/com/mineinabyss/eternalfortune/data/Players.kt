package com.mineinabyss.eternalfortune.data

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column
import java.util.*

object Players : IdTable<UUID>() {
    val playerUUID = uuid("playerUUID").uniqueIndex()
    var graveUUID = uuid("graveUUID").references(Graves.entityUUID)
    override val id: Column<EntityID<UUID>>
        get() = playerUUID.entityId()

}