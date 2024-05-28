package ru.nsu.hybrid.cf.evaluator

import ru.nsu.hybrid.cf.commandDesc.entry.Command
import ru.nsu.hybrid.cf.commandDesc.entry.SubEntry
import ru.nsu.hybrid.cf.commandDesc.option.ChoiceOptionSet
import ru.nsu.hybrid.cf.commandDesc.option.Option
import ru.nsu.hybrid.cf.commandDesc.option.ToggleOptionSet

fun identifier(value: Any, suffix: String = ""): String {
    val id = when (value) {
        is Option -> Identifier.Prefix.OPTION + value.hashCode().toString()
        is ToggleOptionSet -> Identifier.Prefix.TOGGLES_OPTION_SET + value.hashCode().toString()
        is ChoiceOptionSet -> Identifier.Prefix.CHOICE_OPTION_SET + value.hashCode().toString()
        is SubEntry -> Identifier.Prefix.SUB_ENTRY + value.hashCode().toString()
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
        const val VALUE: String = "value"
        const val HANDLER: String = "handler"
        const val DESC = "desc"
        const val ID_MAP = "id_map"
    }

    object Prefix {
        const val COMMAND = "command_frame_"
        const val SUB_ENTRY = "sub_entry_"
        const val CHOICE_OPTION_SET = "option_choice_"
        const val TOGGLES_OPTION_SET = "option_toggles_"
        const val OPTION = "option_"
    }
}