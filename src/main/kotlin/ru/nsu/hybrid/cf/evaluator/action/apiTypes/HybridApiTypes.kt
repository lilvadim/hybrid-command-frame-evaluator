package ru.nsu.hybrid.cf.evaluator.action.apiTypes

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonRawValue

data class CommandOptionDescription(
    val optionSynonyms: List<String>,
    val hasValue: Boolean
)

data class CommandDescription(
    val command: String,
    val subcommands: List<String>?,
    val options: List<CommandOptionDescription>
)

enum class OptionType {
    UNIX,
    GNU,
    @JsonProperty("NON-STD")
    NON_STD
}

data class AddOption(
    val optionType: OptionType,
    val optionText: String,
    @JsonRawValue val value: String?,
    val delimiter: String?,
    val words: List<String>?,
)

data class RemoveOption(
    val optionText: String,
    val removeValue: Boolean,
)

data class UpdateOptionsParameters(
    val addOptions: List<AddOption>,
    val removeOptions: List<RemoveOption>
)


