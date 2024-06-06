package ru.nsu.hybrid.dsl.api

import ru.nsu.hybrid.cf.commandDesc.SetType
import ru.nsu.hybrid.cf.commandDesc.entry.ComplexCommand
import ru.nsu.hybrid.cf.commandDesc.entry.SimpleCommand
import ru.nsu.hybrid.cf.commandDesc.entry.SubCommand
import ru.nsu.hybrid.cf.commandDesc.entry.SubEntry
import ru.nsu.hybrid.cf.commandDesc.option.Option
import ru.nsu.hybrid.cf.commandDesc.option.OptionExpr
import ru.nsu.hybrid.cf.commandDesc.option.OptionSet
import ru.nsu.hybrid.cf.commandDesc.option.effect.SideEffect
import ru.nsu.hybrid.dsl.ext.OptionBuilderExtensionMixin
import ru.nsu.hybrid.dsl.mapper.DslMapper

@DslMarker
annotation class HybridDsl

@HybridDsl
class OptionContext(
    val optionVariants: Set<OptionExpr>
) : OptionBuilderExtensionMixin {
    override val ctx = this

    val sideEffects: MutableSet<SideEffect> = mutableSetOf()
    var values: Set<String>? = null
    var description: String? = null
    var exclusiveInGroups: Set<String>? = null
    var inclusiveInGroups: Set<String>? = null

    fun include(vararg optionRefs: String) {
        optionRefs.forEach{ sideEffects += SideEffect.include(it) }
    }

    fun exclude(vararg optionRefs: String) {
        optionRefs.forEach{ sideEffects += SideEffect.exclude(it) }
    }

    fun values(vararg values: String) {
        this.values = values.toSet()
    }

    fun description(description: String) {
        this.description = description
    }

    fun exclusiveInGroups(vararg groupIds: String) {
        this.exclusiveInGroups = groupIds.toSet()
    }

    fun inclusiveInGroups(vararg groupIds: String) {
        this.inclusiveInGroups = groupIds.toSet()
    }

    fun validated(): OptionContext {
        if (optionVariants.isEmpty()) {
            throw IllegalStateException("Option variants is empty")
        }

        val allExpressionsHaveArg = optionVariants.all { it.hasArg }
        val allExpressionsHaveNotArg = optionVariants.all { !it.hasArg }

        val valid = allExpressionsHaveNotArg || allExpressionsHaveArg

        if (!valid) {
            throw IllegalStateException("All option variants must either have or have not argument: $optionVariants")
        }

        return this
    }
}

@HybridDsl
abstract class EntryContext(
    val name: String
) {
    val options: MutableList<OptionSet> = mutableListOf()

    fun choice(config: OptionSetContext.() -> Unit) {
        val context = OptionSetContext().apply(config)
        options += OptionSet(context, SetType.ALTERNATE)
    }

    fun toggles(config: OptionSetContext.() -> Unit) {
        val context = OptionSetContext().apply(config)
        options += OptionSet(context, SetType.ANY)
    }
}

@HybridDsl
open class OptionSetContext(val set: MutableSet<Option> = mutableSetOf()) : MutableSet<Option> by set {
    fun option(vararg optionVariants: String, config: OptionContext.() -> Unit = {}) {
        val context = OptionContext(optionVariants.map { OptionExpr(it) }.toSet()).apply(config).validated()
        val option = DslMapper.instance.option(context)
        this += option
    }
}

val alternate = SetType.ALTERNATE
val any = SetType.ANY

@HybridDsl
open class SubEntryContext(name: String, var setType: SetType) : EntryContext(name) {
    val entries: MutableList<SubEntry> = mutableListOf()

    fun alternate() {
        setType = SetType.ALTERNATE
    }

    fun entry(name: String, config: SubEntryContext.() -> Unit) {
        val context = SubEntryContext(name, SetType.ANY).apply(config)
        val entry = DslMapper.instance.inlineEntry(context)
        entries += entry
    }

    fun tabs(name: String = "", setType: SetType = SetType.ANY, config: SubEntryContext.() -> Unit) {
        val context = SubEntryContext(name, setType).apply(config)
        val tabEntry = DslMapper.instance.tabEntry(context)
        entries += tabEntry
    }
}

@HybridDsl
open class CommandContext(name: String) : EntryContext(name) {
    val entries: MutableList<SubEntry> = mutableListOf()

    fun tabs(name: String = "", setType: SetType = SetType.ANY, config: SubEntryContext.() -> Unit) {
        val context = SubEntryContext(name, setType).apply(config)
        val tabEntry = DslMapper.instance.tabEntry(context)
        entries += tabEntry
    }

    fun entry(name: String, config: SubEntryContext.() -> Unit) {
        val context = SubEntryContext(name, SetType.ANY).apply(config)
        val inlineEntry = DslMapper.instance.inlineEntry(context)
        entries += inlineEntry
    }
}

@HybridDsl
class SubCommandContext(
    name: String,
    val parentCommandName: String
) : CommandContext(name)

@HybridDsl
class ComplexCommandContext(name: String) : CommandContext(name) {
    val subcommands: MutableList<SubCommand> = mutableListOf()

    fun subcommand(name: String, config: SubCommandContext.() -> Unit) {
        val context = SubCommandContext(name, this.name).apply(config)
        val subcommand = DslMapper.instance.subCommand(context)
        subcommands += subcommand
    }
}

@HybridDsl
fun simpleCommand(name: String, config: CommandContext.() -> Unit): SimpleCommand {
    val context = CommandContext(name).apply(config)
    return DslMapper.instance.simpleCommand(context)
}

@HybridDsl
fun complexCommand(name: String, config: ComplexCommandContext.() -> Unit): ComplexCommand {
    val context = ComplexCommandContext(name).apply(config)
    return DslMapper.instance.complexCommand(context)
}