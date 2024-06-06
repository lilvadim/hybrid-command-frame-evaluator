package ru.nsu.hybrid.cf.commandDesc.option

import ru.nsu.hybrid.cf.commandDesc.SetType

class OptionSet(
    val set: Set<Option>,
    val setType: SetType,
) : Set<Option> by set