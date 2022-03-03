package com.mineinabyss.eternalfortune

import com.mineinabyss.idofront.commands.Command
import com.mineinabyss.idofront.commands.CommandHolder
import com.mineinabyss.idofront.commands.execution.IdofrontCommandExecutor

object EternalFortuneCommandExecutor : IdofrontCommandExecutor() {
    override val commands: CommandHolder = commands(eternalFortunePlugin) {
        ("eternalfortune" / "ef")(desc = "Commands related to Eternal Fortune graves") {

        }
    }
}