package ru.nsu.hybrid.cf.commandDesc.entry

sealed class EntrySet<E : Entry>(
    val values: List<E>
)

class InlineEntrySet(
    values: List<SubEntry>
) : EntrySet<SubEntry>(values)

class ChoiceEntrySet(
    values: List<InlineEntry>
) : EntrySet<InlineEntry>(values)