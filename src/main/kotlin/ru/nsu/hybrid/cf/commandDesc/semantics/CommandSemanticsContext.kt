package ru.nsu.hybrid.cf.commandDesc.semantics

import ru.nsu.hybrid.cf.commandDesc.entry.ComplexCommand
import ru.nsu.hybrid.cf.commandDesc.entry.SimpleCommand
import ru.nsu.hybrid.cf.commandDesc.option.ChoiceOptionSet
import ru.nsu.hybrid.cf.commandDesc.option.Option
import ru.nsu.hybrid.cf.commandDesc.option.OptionRef
import ru.nsu.hybrid.cf.commandDesc.option.OptionSet
import ru.nsu.hybrid.cf.commandDesc.option.effect.SideEffect

class SimpleCommandSemanticsContext(
    val command: SimpleCommand,
    private val groups: Map<String, List<OptionRef>>,
    val flattenOptionSets: List<OptionSet>,
) {
    private val optionSemanticsCache = mutableMapOf<OptionRef, OptionSemantics>()
    private val optionsByRef: Map<OptionRef, Option> = buildMap {
        flattenOptionSets.forEach { set ->
            set.forEach { opt ->
                val refs = opt.references()
                refs.forEach { ref ->
                    put(ref, opt)
                }
            }
        }
    }

    private val optionSetByRef: Map<OptionRef, OptionSet> = buildMap {
        flattenOptionSets.forEach { set ->
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

    private fun collectWhenToggleOffEffect(optionRef: OptionRef): Effect {
        return Effect(
            include = emptyList(),
            exclude = listOf(optionRef)
        )
    }

    private fun collectWhenToggleOnEffect(optionRef: OptionRef): Effect {
        return Effect(
            include = collectIncludedOptions(optionRef),
            exclude = collectExcludedOptions(optionRef)
        )
    }

    private fun collectExcludedOptions(optionRef: OptionRef): List<OptionRef> {
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

    private fun collectIncludedOptions(optionRef: OptionRef): List<OptionRef> {
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

data class Effect(
    val include: List<OptionRef>,
    val exclude: List<OptionRef>
)

data class OptionSemantics(
    val optionRef: OptionRef,
    val whenToggleOn: Effect,
    val whenToggleOff: Effect
)

class ComplexCommandSemanticsContext(
    val command: ComplexCommand,
    val subcommandsSemantics: Map<String, SimpleCommandSemanticsContext>
)