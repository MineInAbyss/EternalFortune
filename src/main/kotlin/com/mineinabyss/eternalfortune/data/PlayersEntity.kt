package com.mineinabyss.eternalfortune.data

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

class PlayersEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object: UUIDEntityClass<PlayersEntity>(Players)
}