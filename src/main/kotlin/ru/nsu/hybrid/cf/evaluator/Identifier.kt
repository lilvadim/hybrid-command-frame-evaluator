package ru.nsu.hybrid.cf.evaluator

import ru.nsu.hybrid.cf.commandDesc.SetType
import ru.nsu.hybrid.cf.commandDesc.entry.Command
import ru.nsu.hybrid.cf.commandDesc.entry.SubCommand
import ru.nsu.hybrid.cf.commandDesc.entry.SubEntry
import ru.nsu.hybrid.cf.commandDesc.option.Option
import ru.nsu.hybrid.cf.commandDesc.option.OptionSet

fun identifier(value: Any, suffix: String = ""): String {
    val id = when (value) {
        is Option -> Identifier.Prefix.OPTION + value.hashCode().toString()
        is OptionSet -> when (value.setType) {
            SetType.ANY -> Identifier.Prefix.TOGGLES_OPTION_SET + value.hashCode().toString()
            SetType.ALTERNATE -> Identifier.Prefix.CHOICE_OPTION_SET + value.hashCode().toString()
        }
        is SubEntry -> Identifier.Prefix.SUB_ENTRY + value.hashCode().toString()
        is SubCommand -> Identifier.Prefix.COMMAND + value.parentCommandName + "_" + value.name
        is Command -> Identifier.Prefix.COMMAND + value.name
        else -> throw IllegalArgumentException()
    }

    return listOf(id, suffix).filter { it.isNotBlank() }.joinToString(separator = "_")
}

fun commandIdentifier(commandName: String, suffix: String = ""): String {
    return Identifier.Prefix.COMMAND + listOf(commandName, suffix).filter { it.isNotBlank() }.joinToString(separator = "_")
}

object Identifier {
    object Suffix {
        const val TAB_PANE: String = "tab_pane"
        const val TAB: String = "tab"
        const val SEMANTIC: String = "semantic"
        const val VALUE: String = "value"
        const val HANDLER: String = "handler"
        const val SYNTAX = "syntax"
        const val ID_MAP = "id_map"
        const val SHOW = "show"
        const val HIDE = "hide"
    }

    object Prefix {
        const val COMMAND = "command_frame_"
        const val SUB_ENTRY = "sub_entry_"
        const val CHOICE_OPTION_SET = "option_choice_"
        const val TOGGLES_OPTION_SET = "option_toggles_"
        const val OPTION = "option_"
    }
}