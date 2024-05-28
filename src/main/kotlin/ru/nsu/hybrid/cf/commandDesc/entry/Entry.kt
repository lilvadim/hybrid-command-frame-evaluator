package ru.nsu.hybrid.cf.commandDesc.entry

import ru.nsu.hybrid.cf.commandDesc.option.OptionSet

sealed class Entry(
    val name: String,
    val options: List<OptionSet>?,
    open val entries: List<Entry>?,
)

