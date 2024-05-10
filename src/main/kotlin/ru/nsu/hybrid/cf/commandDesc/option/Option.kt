package ru.nsu.hybrid.cf.commandDesc.option

import ru.nsu.hybrid.cf.commandDesc.SideEffect

class Option(
    val optionVariants: Set<OptionExpr>,
    val sideEffects: Set<SideEffect>?,
    val description: String?,
    val values: Set<String>?,
    val exclusiveInGroups: Set<String>?,
    val inclusiveInGroups: Set<String>?
) {
    val hasValue: Boolean = (values?.isNotEmpty() ?: false) || (optionVariants.any { it.hasArg })

    init {
        validate()
    }

    fun isReferenced(optionRef: OptionRef): Boolean = optionVariants.any { it.reference == optionRef }

    fun references(): List<OptionRef> = optionVariants.map { it.reference }

    private fun validate() {
        if (optionVariants.isEmpty()) {
            throw IllegalStateException("Option variants is empty")
        }

        val allExpressionsHaveArg = optionVariants.all { it.hasArg }
        val allExpressionsHaveNotArg = optionVariants.all { !it.hasArg }

        val valid = allExpressionsHaveNotArg || allExpressionsHaveArg

        if (!valid) {
            throw IllegalStateException("All option variants must either have or have not argument: $optionVariants")
        }
    }
}