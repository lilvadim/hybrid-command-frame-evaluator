package ru.nsu.hybrid.cf.commandDesc.entry

import ru.nsu.hybrid.cf.commandDesc.SetType
import ru.nsu.hybrid.cf.commandDesc.option.OptionSet

sealed class SubEntry(
    val childrenLayout: ChildrenLayout,
    val setType: SetType,
    name: String,
    options: List<OptionSet>?,
    override val entries: List<SubEntry>?
) : Entry(name, options, entries)

class TabEntry(
    name: String,
    options: List<OptionSet>?,
    setType: SetType,
    entries: List<SubEntry>?
) : SubEntry(ChildrenLayout.TABS, setType, name, options, entries)

class InlineEntry(
    name: String,
    options: List<OptionSet>?,
    entries: List<SubEntry>?
) : SubEntry(ChildrenLayout.INLINE, SetType.ANY, name, options, entries)