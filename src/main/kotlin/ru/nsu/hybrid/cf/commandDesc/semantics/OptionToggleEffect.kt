package ru.nsu.hybrid.cf.commandDesc.semantics

import ru.nsu.hybrid.cf.commandDesc.option.OptionRef

data class OptionToggleEffect(
    val include: List<OptionRef>,
    val exclude: List<OptionRef>
)