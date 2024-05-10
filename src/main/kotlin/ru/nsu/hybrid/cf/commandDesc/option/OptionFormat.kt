package ru.nsu.hybrid.cf.commandDesc.option

object OptionFormat {
    val option = Regex("^(-|--)[^\\s<>]+(\\s)*(<[^\\s<>]*>)*\$")
    val optionWithArg = Regex("^(-|--)[^\\s<>]+(\\s)*(<[^\\s<>]*>)\$")
    val optionArg = Regex("<[^\\s<>]*>")

    object ValueSeparator {
        val notSeparatorFormat = Regex("-|\\w")
        val knownSeparators = listOf("=", ",")
    }
}

fun isNotSeparator(value: CharSequence) = OptionFormat.ValueSeparator.notSeparatorFormat.matches(value)
fun isOptionValueSeparator(value: CharSequence) = !isNotSeparator(value)