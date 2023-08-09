package com.mineinabyss.eternalfortune

import com.mineinabyss.geary.prefabs.PrefabKey
import com.mineinabyss.geary.prefabs.serializers.PrefabKeySerializer
import com.mineinabyss.idofront.serialization.DurationSerializer
import kotlinx.serialization.Serializable
import kotlin.time.Duration

@Serializable
data class EternalConfig(
    val graveFurniture: @Serializable(PrefabKeySerializer::class) PrefabKey,
    val maxGraveCount: Int,
    val protectionTime: @Serializable(DurationSerializer::class) Duration,
    val expirationTime: @Serializable(DurationSerializer::class) Duration,
    val spawnRadiusCheck: Int
)
