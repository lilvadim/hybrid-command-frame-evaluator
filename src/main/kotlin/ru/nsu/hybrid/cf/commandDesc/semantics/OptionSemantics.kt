package ru.nsu.hybrid.cf.commandDesc.semantics

import ru.nsu.hybrid.cf.commandDesc.option.OptionRef

data class OptionSemantics(
    val optionRef: OptionRef,
    val whenToggleOn: OptionToggleEffect,
    val whenToggleOff: OptionToggleEffect
)