package ru.nsu.hybrid.dsl.mapper

import org.mapstruct.BeanMapping
import org.mapstruct.Mapper
import org.mapstruct.ReportingPolicy
import org.mapstruct.factory.Mappers
import ru.nsu.hybrid.cf.commandDesc.entry.*
import ru.nsu.hybrid.cf.commandDesc.option.Option
import ru.nsu.hybrid.dsl.builder.*

@Mapper(
    unmappedSourcePolicy = ReportingPolicy.ERROR,
    unmappedTargetPolicy = ReportingPolicy.ERROR,
)
abstract class DslMapper {

    @BeanMapping(ignoreUnmappedSourceProperties = ["exclusive", "inclusive", "toggle", "ctx"])
    abstract fun option(optionBuilder: OptionBuilder): Option

    abstract fun inlineEntry(entryBuilder: SubEntryBuilder): InlineEntry

    abstract fun tabEntry(entryBuilder: SubEntryBuilder): TabEntry

    abstract fun simpleCommand(commandBuilder: CommandBuilder): SimpleCommand

    abstract fun subCommand(subCommandBuilder: SubCommandBuilder): SubCommand

    abstract fun complexCommand(complexCommandBuilder: ComplexCommandBuilder): ComplexCommand

    companion object {
        val instance: DslMapper = Mappers.getMapper(DslMapper::class.java)
    }
}