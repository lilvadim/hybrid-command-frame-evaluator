package ru.nsu.hybrid.cf.commandDesc.semantics

import ru.nsu.hybrid.cf.commandDesc.entry.Command
import ru.nsu.hybrid.cf.commandDesc.option.ChoiceOptionSet
import ru.nsu.hybrid.cf.commandDesc.option.Option
import ru.nsu.hybrid.cf.commandDesc.option.OptionRef
import ru.nsu.hybrid.cf.commandDesc.option.OptionSet
import ru.nsu.hybrid.cf.commandDesc.option.effect.SideEffect

class CommandSemanticsContext(
    val command: Command,
    val groups: Map<String, List<OptionRef>>,
    val optionSets: List<OptionSet>,
) {
    private val optionSemanticsCache = mutableMapOf<OptionRef, OptionSemantics>()

    val optionsByRef: Map<OptionRef, Option> = optionSets.flatten().flatMap { opt ->
        opt.references().map { it to opt }
    }.toMap()

    private val optionSetByRef: Map<OptionRef, OptionSet> = buildMap {
        optionSets.forEach { set ->
            set.forEach { opt ->
                val refs = opt.references()
                refs.forEach { ref ->
                    put(ref, set)
                }
            }
        }
    }

    fun optionSemantics(optionRef: OptionRef): OptionSemantics = optionSemanticsCache.getOrPut(optionRef) {
        OptionSemantics(
            optionRef,
            whenToggleOn = collectWhenToggleOnEffect(optionRef),
            whenToggleOff = collectWhenToggleOffEffect(optionRef)
        )
    }

    private fun collectWhenToggleOffEffect(optionRef: OptionRef): OptionToggleEffect {
        return OptionToggleEffect(
            include = emptyList(),
            exclude = listOf(optionRef)
        )
    }

    private fun collectWhenToggleOnEffect(optionRef: OptionRef): OptionToggleEffect {
        return OptionToggleEffect(
            include = collectWhenToggleOnIncludeOptions(optionRef),
            exclude = collectWhenToggleOnExcludeOptions(optionRef)
        )
    }

    private fun collectWhenToggleOnExcludeOptions(optionRef: OptionRef): List<OptionRef> {
        val exclude = mutableListOf<OptionRef>()
        val option = optionsByRef[optionRef]
        option?.sideEffects?.filter { it.effectType == SideEffect.Type.INCLUDE }?.forEach { exclude += it.optionRef }
        option?.exclusiveInGroups?.forEach { groupId ->
            val group = groups[groupId]
            group?.filter { it != optionRef }?.forEach { ref ->
                exclude += ref
            }
        }
        val set = optionSetByRef[optionRef]
        if (set is ChoiceOptionSet) {
            set.filterNot { it.isReferenced(optionRef) }.map { it.references().first() }.forEach { exclude += it }
        }
        return exclude.distinct()
    }

    private fun collectWhenToggleOnIncludeOptions(optionRef: OptionRef): List<OptionRef> {
        val include = mutableListOf(optionRef)
        val option = optionsByRef[optionRef]
        option?.sideEffects?.filter { it.effectType == SideEffect.Type.EXCLUDE }?.forEach { include += it.optionRef }
        option?.inclusiveInGroups?.forEach { groupId ->
            val group = groups[groupId]
            group?.filter { it != optionRef }?.forEach { ref ->
                include += ref
            }
        }
        return include.distinct()
    }
}

