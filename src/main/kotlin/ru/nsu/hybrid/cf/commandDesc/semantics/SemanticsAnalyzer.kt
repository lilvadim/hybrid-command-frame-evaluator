package ru.nsu.hybrid.cf.commandDesc.semantics

import ru.nsu.hybrid.cf.commandDesc.entry.Command
import ru.nsu.hybrid.cf.commandDesc.entry.ComplexCommand
import ru.nsu.hybrid.cf.commandDesc.entry.SimpleCommand
import ru.nsu.hybrid.cf.commandDesc.option.OptionRef
import ru.nsu.hybrid.cf.commandDesc.option.OptionSet
import ru.nsu.hybrid.cf.commandDesc.entry.traverse

class SemanticsAnalyzer {

    private val commandSemanticsCache = mutableMapOf<Command, SimpleCommandSemanticsContext>()

    fun commandSemantics(command: ComplexCommand): ComplexCommandSemanticsContext {
        return ComplexCommandSemanticsContext(
            command,
            subcommandsSemantics = command.subcommands?.associateBy({ it.name }, { commandSemantics(it) }) ?: emptyMap()
        )
    }

    fun commandSemantics(command: SimpleCommand): SimpleCommandSemanticsContext {
        return commandSemanticsCache.getOrPut(command) {
            val flattenOptionSets = mutableListOf<OptionSet>()
            val groups = mutableMapOf<String, MutableList<OptionRef>>()
            traverse(command) {
                flattenOptionSets += it.options ?: emptyList()
                it.options?.forEach { set ->
                    set.forEach { option ->
                        option.inclusiveInGroups?.forEach { groupId ->
                            groups.getOrPut(groupId) { mutableListOf() }.addAll(option.references())
                        }
                        option.exclusiveInGroups?.forEach { groupId ->
                            groups.getOrPut(groupId) { mutableListOf() }.addAll(option.references())
                        }
                    }
                }
            }
            SimpleCommandSemanticsContext(command, groups, flattenOptionSets)
        }
    }
}