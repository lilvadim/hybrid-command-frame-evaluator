package ru.nsu.hybrid.cf.commandDesc.entry

import ru.nsu.hybrid.cf.commandDesc.option.OptionSet

sealed class SubEntry(
    name: String,
    options: List<OptionSet>?,
    override val entries: List<SubEntry>?
) : Entry(name, options, entries)

class TabEntry(
    name: String,
    options: List<OptionSet>?,
    entries: List<SubEntry>?
) : SubEntry(name, options, entries)

class InlineEntry(
    name: String,
    options: List<OptionSet>?,
    entries: List<SubEntry>?
) : SubEntry(name, options, entries)