package com.mineinabyss.eternalfortune

import com.mineinabyss.idofront.commands.entrypoint.CommandDSLEntrypoint
import com.mineinabyss.idofront.commands.execution.IdofrontCommandExecutor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class EternalCommands : IdofrontCommandExecutor(), TabCompleter {

    override val commands = commands(eternal.plugin) {
        command("eternal", "ef") {

        }
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): List<String> {
        if (command.label != "eternal") return emptyList()
        when (args.size) {
            1 -> listOf("fortune").filter { it.startsWith(args[0]) }
            else -> emptyList()
        }
        return emptyList()
    }
}
