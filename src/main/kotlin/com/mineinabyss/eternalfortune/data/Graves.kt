package com.mineinabyss.eternalfortune.data

import com.mineinabyss.eternalfortune.EternalFortuneConfig
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.javatime.duration
import java.time.LocalDateTime
import java.util.*
import kotlin.time.toJavaDuration

object Graves : IdTable<UUID>() {
    val entityUUID = uuid("entityUUID").uniqueIndex()
    val ownerUUID = uuid("ownerUUID").nullable()
    //val location = location("location")
    val stateChangedTimestamp = datetime("stateChangedTimestamp").clientDefault { LocalDateTime.now() }
    val protectionTimeRemaining = duration("protectionTimeRemaining")
        .default(EternalFortuneConfig.data.graveProtectionTime.toJavaDuration())
    override val id: Column<EntityID<UUID>>
        get() = entityUUID.entityId()
}