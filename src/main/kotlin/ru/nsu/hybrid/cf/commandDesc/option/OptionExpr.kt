package ru.nsu.hybrid.cf.commandDesc.option

/**
 * Class describing option expression such as "--opt=<arg>"
 */
data class OptionExpr(
    val value: String,
) {
    val hasArg = value.matches(OptionFormat.optionWithArg)

    val reference: OptionRef = OptionRef(reference())
    val pattern: String = pattern()
    val arg: String = arg()

    init {
        validate()
    }

    private fun reference(): String = value.replace(OptionFormat.optionArg, "").trimEnd {
        isOptionValueSeparator(it.toString())
    }

    private fun pattern(): String = value.replace(OptionFormat.optionArg, "").trimEnd()

    private fun arg() = value.removePrefix(pattern).replace(Regex("[<>]"), "").trim()

    private fun validate() {
        val valid = value.matches(OptionFormat.option)
        if (!valid) {
            throw IllegalStateException("Wrong format, option: $value")
        }
    }
}