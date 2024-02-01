package com.mineinabyss.eternalfortune

import com.mineinabyss.geary.prefabs.PrefabKey
import com.mineinabyss.geary.prefabs.serializers.PrefabKeySerializer
import com.mineinabyss.idofront.serialization.DurationSerializer
import kotlinx.serialization.Serializable
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days

@Serializable
data class EternalConfig(
    val graveFurniture: @Serializable(PrefabKeySerializer::class) PrefabKey = PrefabKey.of("mineinabyss", "grave"),
    val maxGraveCount: Int = 1,
    val protectionTime: @Serializable(DurationSerializer::class) Duration = 7.days,
    val expirationTime: @Serializable(DurationSerializer::class) Duration = 7.days,
    val spawnRadiusCheck: Int = 1,
    val ignoreKeepInv: Boolean = false,
    val keepExp: Boolean = true,
    val textDisplayOffset: Double = 1.5,
)
