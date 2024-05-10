package ru.nsu.hybrid.cf.evaluator.types.mapper

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.ReportingPolicy
import org.mapstruct.factory.Mappers
import ru.nsu.hybrid.cf.commandDesc.entry.Command
import ru.nsu.hybrid.cf.commandDesc.entry.SimpleCommand
import ru.nsu.hybrid.cf.commandDesc.entry.traverse
import ru.nsu.hybrid.cf.commandDesc.option.Option
import ru.nsu.hybrid.cf.commandDesc.option.OptionExpr
import ru.nsu.hybrid.cf.commandDesc.option.OptionRef
import ru.nsu.hybrid.cf.evaluator.types.CommandDescription
import ru.nsu.hybrid.cf.evaluator.types.CommandOption
import ru.nsu.hybrid.cf.evaluator.types.CommandOptionDescription
import ru.nsu.hybrid.cf.evaluator.types.OptionPattern

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    unmappedSourcePolicy = ReportingPolicy.IGNORE,
)
abstract class TypesMapper {

    @Mapping(target = "subcommand", source = "subcommand.name")
    @Mapping(target = "options", source = "subcommand")
    abstract fun toCommandDescription(command: String, subcommand: SimpleCommand): CommandDescription

    @Mapping(target = "subcommand", ignore = true)
    @Mapping(target = "options", source = ".")
    @Mapping(target = "command", source = "name")
    abstract fun toCommandDescription(command: SimpleCommand): CommandDescription

    fun extractOptions(command: SimpleCommand): List<CommandOptionDescription> {
        val result = mutableListOf<CommandOptionDescription>()
        traverse(command) { entry ->
            entry.options?.flatten()?.forEach { result += toOptionDescription(it) }
        }
        return result
    }

    @Mapping(target = "optionPatterns", source = "optionVariants")
    abstract fun toOptionDescription(option: Option): CommandOptionDescription

    @Mapping(target = "pattern", source = "value")
    @Mapping(target = "copy", ignore = true)
    abstract fun toOptionPattern(optionExpr: OptionExpr): OptionPattern

    companion object {
        val instance: TypesMapper = Mappers.getMapper(TypesMapper::class.java)
    }
}