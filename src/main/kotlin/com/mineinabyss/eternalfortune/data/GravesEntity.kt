package com.mineinabyss.eternalfortune.data

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

class GravesEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object: UUIDEntityClass<GravesEntity>(Graves)
}