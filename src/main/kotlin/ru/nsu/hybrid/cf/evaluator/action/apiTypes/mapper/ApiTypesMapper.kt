package ru.nsu.hybrid.cf.evaluator.action.apiTypes.mapper

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.ReportingPolicy
import org.mapstruct.factory.Mappers
import ru.nsu.hybrid.cf.commandDesc.entry.*
import ru.nsu.hybrid.cf.commandDesc.option.Option
import ru.nsu.hybrid.cf.commandDesc.option.OptionExpr
import ru.nsu.hybrid.cf.commandDesc.semantics.CommandSemanticsContext
import ru.nsu.hybrid.cf.evaluator.action.apiTypes.*

const val EMPTY_LIST_EXPR = "java(java.util.Collections.emptyList())"

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    unmappedSourcePolicy = ReportingPolicy.IGNORE,
)
abstract class ApiTypesMapper {

    fun toCommandSyntax(command: Command): CommandSyntax {
        return when (command) {
            is ComplexCommand -> complexCommandToSyntax(command)
            is SimpleCommand -> simpleCommandToSyntax(command)
        }
    }

    @Mapping(target = "command", source = "name")
    @Mapping(target = "options", source = ".")
    abstract fun complexCommandToSyntax(command: ComplexCommand): CommandSyntax

    @Mapping(target = "command", source = "name")
    @Mapping(target = "options", source = ".")
    @Mapping(target = "subcommands", expression = EMPTY_LIST_EXPR)
    abstract fun simpleCommandToSyntax(command: SimpleCommand): CommandSyntax

    fun mapSubcommands(subcommands: List<SubCommand>): List<String> {
        return subcommands.map { it.name }
    }

    fun extractOptions(command: Command): List<OptionSyntax> {
        val result = mutableListOf<OptionSyntax>()
        traverse(command) { entry ->
            entry.options?.flatten()?.forEach { result += toSyntax(it) }
        }
        if (command is ComplexCommand) {
            command.subcommands?.forEach { subcommand ->
                traverse(subcommand) { entry ->
                    entry.options?.flatten()?.forEach { result += toSyntax(it) }
                }
            }
        }
        return result
    }

    @Mapping(target = "optionSynonyms", source = "optionVariants")
    abstract fun toSyntax(option: Option): OptionSyntax

    fun mapSynonyms(variants: Iterable<OptionExpr>): List<String> {
        return variants.map { it.reference.value }
    }

    fun toSemantic(commandSemanticsContext: CommandSemanticsContext): CommandSemantic {
        val options = commandSemanticsContext.optionSets.flatten()
        val optionSemantic = options.map { opt ->
            val s = commandSemanticsContext.optionSemantics(opt.references().first())
            OptionSemantic(
                option = opt.references().first().value,
                whenAdded = Effect(
                    add = s.whenToggleOn.include.mapNotNull { ref ->
                        commandSemanticsContext.optionsByRef[ref] }.map { toAdd -> toAddOption(toAdd) },
                    remove = s.whenToggleOn.exclude.mapNotNull { ref ->
                        commandSemanticsContext.optionsByRef[ref] }.map { toRemove -> toRemoveOption(toRemove) }

                ),
                whenRemoved = Effect(
                    add = s.whenToggleOff.include.mapNotNull { ref ->
                        commandSemanticsContext.optionsByRef[ref] }.map { toAdd -> toAddOption(toAdd) },
                    remove = s.whenToggleOff.exclude.mapNotNull { ref ->
                        commandSemanticsContext.optionsByRef[ref] }.map { toRemove -> toRemoveOption(toRemove) }
                )
            )
        }
        return CommandSemantic(
            command = commandSemanticsContext.command.name,
            options = optionSemantic
        )
    }

    private fun toRemoveOption(toRemove: Option) = RemoveOption(
        optionText = toRemove.references().first().value,
        removeValue = toRemove.hasValue
    )

    private fun toAddOption(
        toAdd: Option,
    ) = AddOption(
        optionType = OptionType.valueOf(toAdd.optionVariants.first().type.name),
        optionText = toAdd.references().first().value,
        value = "undefined",
        delimiter = toAdd.optionVariants.first().delimiter,
        words = null,
        unique = true
    )

    companion object {
        val instance: ApiTypesMapper = Mappers.getMapper(ApiTypesMapper::class.java)
    }
}