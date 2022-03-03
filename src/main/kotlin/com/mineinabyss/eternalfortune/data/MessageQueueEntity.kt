package com.mineinabyss.eternalfortune.data

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class MessageQueueEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object: IntEntityClass<MessageQueueEntity>(MessageQueue)
}