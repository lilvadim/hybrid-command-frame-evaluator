package ru.nsu.hybrid.cf.evaluator.action.apiTypes

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonRawValue

data class OptionSyntax(
    val optionSynonyms: List<String>,
    val hasValue: Boolean
)

data class CommandSyntax(
    val command: String,
    val subcommands: List<String>?,
    val options: List<OptionSyntax>
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
    val unique: Boolean,
)

data class RemoveOption(
    val optionText: String,
    val removeValue: Boolean,
)

data class UpdateOptionsParameters(
    val addOptions: List<AddOption>,
    val removeOptions: List<RemoveOption>
)

data class CommandSemantic(
    val command: String,
    val options: List<OptionSemantic>
)

data class OptionSemantic(
    val option: String,
    val whenAdded: Effect,
    val whenRemoved: Effect
)

data class Effect(
    val add: List<AddOption>,
    val remove: List<RemoveOption>
)

