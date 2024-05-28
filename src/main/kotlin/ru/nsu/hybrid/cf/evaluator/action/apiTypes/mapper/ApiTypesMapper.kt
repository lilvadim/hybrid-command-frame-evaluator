package ru.nsu.hybrid.cf.evaluator.action.apiTypes.mapper

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.ReportingPolicy
import org.mapstruct.factory.Mappers
import ru.nsu.hybrid.cf.commandDesc.entry.*
import ru.nsu.hybrid.cf.commandDesc.option.Option
import ru.nsu.hybrid.cf.commandDesc.option.OptionExpr
import ru.nsu.hybrid.cf.evaluator.action.apiTypes.CommandDescription
import ru.nsu.hybrid.cf.evaluator.action.apiTypes.CommandOptionDescription

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    unmappedSourcePolicy = ReportingPolicy.IGNORE,
)
abstract class ApiTypesMapper {

    fun toCommandDescription(command: Command): CommandDescription {
        return when (command) {
            is ComplexCommand -> complexCommandToCommandDescription(command)
            is SimpleCommand -> simpleCommandToCommandDescription(command)
        }
    }

    @Mapping(target = "command", source = "name")
    @Mapping(target = "options", source = ".")
    abstract fun complexCommandToCommandDescription(command: ComplexCommand): CommandDescription

    @Mapping(target = "command", source = "name")
    @Mapping(target = "options", source = ".")
    @Mapping(target = "subcommands", expression = "java(java.util.Collections.emptyList())")
    abstract fun simpleCommandToCommandDescription(command: SimpleCommand): CommandDescription

    fun mapSubcommands(subcommands: List<SubCommand>): List<String> {
        return subcommands.map { it.name }
    }

    fun extractOptions(command: Command): List<CommandOptionDescription> {
        val result = mutableListOf<CommandOptionDescription>()
        traverse(command) { entry ->
            entry.options?.flatten()?.forEach { result += toOptionDescription(it) }
        }
        if (command is ComplexCommand) {
            command.subcommands?.forEach { subcommand ->
                traverse(subcommand) { entry ->
                    entry.options?.flatten()?.forEach { result += toOptionDescription(it) }
                }
            }
        }
        return result
    }

    @Mapping(target = "optionSynonyms", source = "optionVariants")
    abstract fun toOptionDescription(option: Option): CommandOptionDescription

    fun mapSynonyms(variants: Iterable<OptionExpr>): List<String> {
        return variants.map { it.reference.value }
    }

    companion object {
        val instance: ApiTypesMapper = Mappers.getMapper(ApiTypesMapper::class.java)
    }
}