@file:UseSerializers(DurationSerializer::class)

package com.mineinabyss.eternalfortune

import com.mineinabyss.idofront.config.IdofrontConfig
import com.mineinabyss.idofront.serialization.DurationSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days

object EternalFortuneConfig : IdofrontConfig<EternalFortuneConfig.Data>(eternalFortunePlugin, Data.serializer()) {
    @Serializable
    data class Data(
        val graveProtectionTime: Duration = 7.days
    )
}