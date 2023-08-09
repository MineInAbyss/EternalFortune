package com.mineinabyss.eternalfortune.components

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("eternalfortune:grave_offline_notice")
data class GraveOfflineNotice(val message: String)
