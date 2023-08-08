package com.mineinabyss.eternalfortune

import com.mineinabyss.idofront.serialization.DurationSerializer
import kotlinx.serialization.Serializable
import kotlin.time.Duration

@Serializable
data class EternalConfig(
    val graveProtectionTime: @Serializable(DurationSerializer::class) Duration,
)
