package ru.nsu.hybrid.cf.commandDesc.semantics

import ru.nsu.hybrid.cf.commandDesc.entry.Command
import ru.nsu.hybrid.cf.commandDesc.entry.Entry
import ru.nsu.hybrid.cf.commandDesc.entry.traverse
import ru.nsu.hybrid.cf.commandDesc.option.OptionRef
import ru.nsu.hybrid.cf.commandDesc.option.OptionSet

class CommandSemanticsAnalyzer {

    private val cache = mutableMapOf<String, CommandSemanticsContext>()

    fun commandSemantics(command: Command): CommandSemanticsContext {
        return cache.getOrPut(command.name) {

            val optionSets = mutableListOf<OptionSet>()
            val groups = mutableMapOf<String, MutableList<OptionRef>>()

            traverse(command) { entry -> collectOptionsInfo(entry, optionSets, groups) }

            CommandSemanticsContext(command, groups, optionSets)
        }
    }

    private fun collectOptionsInfo(
        entry: Entry,
        optionSets: MutableList<OptionSet>,
        groups: MutableMap<String, MutableList<OptionRef>>
    ) {
        optionSets += entry.options ?: emptyList()
        entry.options?.forEach { set ->
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
}