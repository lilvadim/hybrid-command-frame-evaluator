package ru.nsu.hybrid.dsl.builder

import ru.nsu.hybrid.cf.commandDesc.entry.*
import ru.nsu.hybrid.cf.commandDesc.option.*
import ru.nsu.hybrid.cf.commandDesc.option.effect.SideEffect
import ru.nsu.hybrid.dsl.ext.OptionBuilderExtensionMixin
import ru.nsu.hybrid.dsl.mapper.DslMapper

@DslMarker
annotation class HybridDsl

@HybridDsl
class OptionBuilder(
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
}

@HybridDsl
abstract class EntryBuilder(
    val name: String
) {
    val options: MutableList<OptionSet> = mutableListOf()

    fun choice(config: OptionSetBuilder.() -> Unit) {
        val builder = OptionSetBuilder().apply(config)
        options += ChoiceOptionSet(builder)
    }

    fun toggles(config: OptionSetBuilder.() -> Unit) {
        val builder = OptionSetBuilder().apply(config)
        options += ToggleOptionSet(builder)
    }
}

@HybridDsl
open class OptionSetBuilder(val set: MutableSet<Option> = mutableSetOf()) : MutableSet<Option> by set {
    fun option(vararg optionVariants: String, config: OptionBuilder.() -> Unit = {}) {
        val builder = OptionBuilder(optionVariants.map { OptionExpr(it) }.toSet()).apply(config)
        val option = DslMapper.instance.option(builder)
        this += option
    }
}

@HybridDsl
open class SubEntryBuilder(name: String) : EntryBuilder(name) {
    val entries: MutableList<InlineEntry> = mutableListOf()

    fun entry(name: String, config: SubEntryBuilder.() -> Unit) {
        val builder = SubEntryBuilder(name).apply(config)
        val entry = DslMapper.instance.inlineEntry(builder)
        entries += entry
    }
}

@HybridDsl
open class CommandBuilder(name: String) : EntryBuilder(name) {
    val entries: MutableList<SubEntry> = mutableListOf()

    fun tab(name: String, config: SubEntryBuilder.() -> Unit) {
        val builder = SubEntryBuilder(name).apply(config)
        val tabEntry = DslMapper.instance.tabEntry(builder)
        entries += tabEntry
    }

    fun entry(name: String, config: SubEntryBuilder.() -> Unit) {
        val builder = SubEntryBuilder(name).apply(config)
        val inlineEntry = DslMapper.instance.inlineEntry(builder)
        entries += inlineEntry
    }
}

@HybridDsl
class SubCommandBuilder(
    name: String,
    val parentCommandName: String
) : CommandBuilder(name)

@HybridDsl
class ComplexCommandBuilder(name: String) : CommandBuilder(name) {
    val subcommands: MutableList<SubCommand> = mutableListOf()

    fun subcommand(name: String, config: SubCommandBuilder.() -> Unit) {
        val builder = SubCommandBuilder(name, this.name).apply(config)
        val subcommand = DslMapper.instance.subCommand(builder)
        subcommands += subcommand
    }
}

@HybridDsl
fun simpleCommand(name: String, config: CommandBuilder.() -> Unit): SimpleCommand {
    val builder = CommandBuilder(name).apply(config)
    return DslMapper.instance.simpleCommand(builder)
}

@HybridDsl
fun complexCommand(name: String, config: ComplexCommandBuilder.() -> Unit): ComplexCommand {
    val builder = ComplexCommandBuilder(name).apply(config)
    return DslMapper.instance.complexCommand(builder)
}