package ru.nsu.hybrid.cf.evaluator

import ru.nsu.hybrid.cf.commandDesc.entry.Command
import ru.nsu.hybrid.cf.commandDesc.entry.ComplexCommand
import ru.nsu.hybrid.cf.commandDesc.entry.SubEntry
import ru.nsu.hybrid.cf.commandDesc.option.ChoiceOptionSet
import ru.nsu.hybrid.cf.commandDesc.option.Option
import ru.nsu.hybrid.cf.commandDesc.option.ToggleOptionSet

fun htmlId(value: Any): String {
    return when (value) {
        is Option -> "option_" + value.hashCode().toString()
        is ToggleOptionSet -> "option_toggles_" + value.hashCode().toString()
        is ChoiceOptionSet -> "option_choice_" + value.hashCode().toString()
        is SubEntry -> "sub_entry_" + value.hashCode().toString()
        is Command -> "command_frame_" + value.name
        else -> value.hashCode().toString()
    }
}