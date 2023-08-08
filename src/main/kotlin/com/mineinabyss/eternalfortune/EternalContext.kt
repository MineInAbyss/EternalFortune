package com.mineinabyss.eternalfortune

import com.mineinabyss.idofront.di.DI

val eternal by DI.observe<EternalContext>()
interface EternalContext {
    val plugin: EternalFortune
    val config: EternalConfig
}
