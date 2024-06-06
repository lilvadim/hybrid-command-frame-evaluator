package ru.nsu.hybrid.dsl.mapper

import org.mapstruct.BeanMapping
import org.mapstruct.Mapper
import org.mapstruct.ReportingPolicy
import org.mapstruct.factory.Mappers
import ru.nsu.hybrid.cf.commandDesc.entry.*
import ru.nsu.hybrid.cf.commandDesc.option.Option
import ru.nsu.hybrid.dsl.api.*

@Mapper(
    unmappedSourcePolicy = ReportingPolicy.ERROR,
    unmappedTargetPolicy = ReportingPolicy.ERROR,
)
abstract class DslMapper {

    @BeanMapping(ignoreUnmappedSourceProperties = ["exclusive", "inclusive", "toggle", "ctx"])
    abstract fun option(optionContext: OptionContext): Option

    @BeanMapping(ignoreUnmappedSourceProperties = ["setType"])
    abstract fun inlineEntry(entryBuilder: SubEntryContext): InlineEntry

    abstract fun tabEntry(entryBuilder: SubEntryContext): TabEntry

    abstract fun simpleCommand(commandBuilder: CommandContext): SimpleCommand

    abstract fun subCommand(subCommandBuilder: SubCommandContext): SubCommand

    abstract fun complexCommand(complexCommandBuilder: ComplexCommandContext): ComplexCommand

    companion object {
        val instance: DslMapper = Mappers.getMapper(DslMapper::class.java)
    }
}