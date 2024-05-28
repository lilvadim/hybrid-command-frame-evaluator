package ru.nsu.hybrid.cf.commandDesc.entry

import ru.nsu.hybrid.cf.commandDesc.option.OptionSet

sealed class Command(
    name: String,
    options: List<OptionSet>?,
    override val entries: List<SubEntry>?
) : Entry(name, options, entries)

open class SimpleCommand(
    name: String,
    options: List<OptionSet>?,
    entries: List<SubEntry>?,
) : Command(name, options, entries)

class ComplexCommand(
    name: String,
    options: List<OptionSet>?,
    entries: List<SubEntry>?,
    val subcommands: List<SubCommand>?,
) : SimpleCommand(name, options, entries)

class SubCommand(
    val parentCommandName: String,
    name: String,
    options: List<OptionSet>?,
    entries: List<SubEntry>?,
) : SimpleCommand(name, options, entries)