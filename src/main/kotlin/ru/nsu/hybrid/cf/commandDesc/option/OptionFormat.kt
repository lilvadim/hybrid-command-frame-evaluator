package ru.nsu.hybrid.cf.commandDesc.option

object OptionFormat {
    val option = Regex("^(-|--)[^\\s<>]+(\\s)*(<[^\\s<>]*>)*\$")
    val optionWithArg = Regex("^(-|--)[^\\s<>]+(\\s)*(<[^\\s<>]*>)\$")
    val optionArg = Regex("<[^\\s<>]*>")
}

fun isOptionValueSeparator(value: CharSequence) = value == "=" || value == ":" || value == " "