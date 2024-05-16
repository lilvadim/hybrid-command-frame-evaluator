package ru.nsu.hybrid.cf.commandDesc.option

import ru.nsu.hybrid.cf.commandDesc.option.effect.SideEffect

class Option(
    val optionVariants: Set<OptionExpr>,
    val sideEffects: Set<SideEffect>?,
    val description: String?,
    val values: Set<String>?,
    val exclusiveInGroups: Set<String>?,
    val inclusiveInGroups: Set<String>?
) {
    val hasValue: Boolean = (values?.isNotEmpty() ?: false) || (optionVariants.any { it.hasArg })

    fun isReferenced(optionRef: OptionRef): Boolean = optionVariants.any { it.reference == optionRef }

    fun references(): List<OptionRef> = optionVariants.map { it.reference }
}