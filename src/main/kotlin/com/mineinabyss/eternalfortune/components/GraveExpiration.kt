package com.mineinabyss.eternalfortune.components

import com.mineinabyss.idofront.serialization.DurationSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Duration

@Serializable
@SerialName("eternalfortune:grave_expiration")
data class GraveExpiration(
    val remainingTime: @Serializable(DurationSerializer::class) Duration,
)
