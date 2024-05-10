package ru.nsu.hybrid.cf.evaluator.types

data class OptionPattern(
    val pattern: String
)

data class CommandOptionDescription(
    val optionPatterns: List<OptionPattern>,
    val hasValue: Boolean
)

data class CommandDescription(
    val command: String,
    val subcommand: String?,
    val options: List<CommandOptionDescription>
)

data class CommandDescriptor(
    val command: String,
    val subcommand: String?
)

data class CommandOption(
    val option: String,
    val index: Int?,
    val value: String?,
)

