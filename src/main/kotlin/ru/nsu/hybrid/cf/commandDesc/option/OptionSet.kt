package ru.nsu.hybrid.cf.commandDesc.option

sealed class OptionSet(set: Set<Option>) : Set<Option> by set

class ToggleOptionSet(
    set: Set<Option>
) : OptionSet(set)

class ChoiceOptionSet(
    set: Set<Option>
) : OptionSet(set)