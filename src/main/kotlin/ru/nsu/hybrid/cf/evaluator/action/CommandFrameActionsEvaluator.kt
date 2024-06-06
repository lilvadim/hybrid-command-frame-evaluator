package ru.nsu.hybrid.cf.evaluator.action

import ru.nsu.hybrid.cf.commandDesc.entry.Command
import ru.nsu.hybrid.cf.commandDesc.entry.ComplexCommand
import ru.nsu.hybrid.cf.commandDesc.entry.SubCommand
import ru.nsu.hybrid.cf.commandDesc.option.Option
import ru.nsu.hybrid.cf.commandDesc.option.OptionRef
import ru.nsu.hybrid.cf.commandDesc.semantics.CommandSemanticsAnalyzer
import ru.nsu.hybrid.cf.commandDesc.semantics.CommandSemanticsContext
import ru.nsu.hybrid.cf.commandDesc.semantics.OptionSemantics
import ru.nsu.hybrid.cf.evaluator.Identifier
import ru.nsu.hybrid.cf.evaluator.action.apiTypes.AddOption
import ru.nsu.hybrid.cf.evaluator.action.apiTypes.OptionType
import ru.nsu.hybrid.cf.evaluator.action.apiTypes.RemoveOption
import ru.nsu.hybrid.cf.evaluator.action.apiTypes.UpdateOptionsParameters
import ru.nsu.hybrid.cf.evaluator.action.apiTypes.mapper.ApiTypesMapper
import ru.nsu.hybrid.cf.evaluator.identifier


class CommandFrameActionsEvaluator(
    private val semanticsAnalyzer: CommandSemanticsAnalyzer = CommandSemanticsAnalyzer(),
    private val jsTemplateBuilder: JsTemplateBuilder = JsTemplateBuilder(),
    private val apiTypesMapper: ApiTypesMapper = ApiTypesMapper.instance
) {
    fun commandFrameActions(command: Command): CommandFrameActions {
        val semantics = semanticsAnalyzer.commandSemantics(command)
        var scriptContext = scriptContext(semantics)
        val actionMap: MutableMap<ActionDescriptor, String> = mutableMapOf()
        for (optionSet in semantics.optionSets) {
            for (option in optionSet) {
                val optionSemantics = semantics.optionSemantics(option.references().first())
                val (handle, handlerDef) = handler(semantics, optionSemantics, option)
                scriptContext += handlerDef
                actionMap[ActionDescriptor(identifier(option))] = handle
            }
        }

        if (command is ComplexCommand) {
            for (subCommand in command.subcommands ?: emptyList()) {
                val handle = subCommandHandle(subCommand)
                actionMap[ActionDescriptor(identifier(subCommand))] = handle
                val handleRemove = removeSubCommandHandle(subCommand)
                actionMap[ActionDescriptor(identifier(command, subCommand.name))] = handleRemove
            }
        }

        return CommandFrameActions(scriptContext, actionMap)
    }

    private fun handler(
        commandSemantics: CommandSemanticsContext,
        optionSemantics: OptionSemantics,
        option: Option,
    ): Pair<String, String> {
        val valueExtractorName = identifier(option, Identifier.Suffix.VALUE)
        val inputId = identifier(option, Identifier.Suffix.VALUE)
        val optionsByRef = commandSemantics.optionsByRef
        val addOptionsWhenToggleOn = mapAddOptions(optionSemantics.whenToggleOn.include, option, valueExtractorName)
        val removeOptionsWhenToggleOn = mapRemoveOptions(optionSemantics.whenToggleOn.exclude, optionsByRef)
        val addOptionsWhenToggleOff = mapAddOptions(optionSemantics.whenToggleOff.include, option, valueExtractorName)
        val removeOptionsWhenToggleOff = mapRemoveOptions(optionSemantics.whenToggleOff.exclude, optionsByRef)

        val handlerName = identifier(option, Identifier.Suffix.HANDLER)

        val updateOptionsWhenToggleOn = UpdateOptionsParameters(
            addOptions = addOptionsWhenToggleOn,
            removeOptions = removeOptionsWhenToggleOn
        )
        val updateOptionsWhenToggleOff = UpdateOptionsParameters(
            addOptions = addOptionsWhenToggleOff,
            removeOptions = removeOptionsWhenToggleOff
        )

        val script = jsTemplateBuilder.handlerDef(
            handlerName,
            identifier(option),
            updateOptionsWhenToggleOn,
            updateOptionsWhenToggleOff,
            valueExtractorName,
            inputId
        )

        return Pair("$handlerName()", script)
    }

    private fun mapRemoveOptions(
        excludeRefs: List<OptionRef>,
        optionsByRef: Map<OptionRef, Option>
    ) = excludeRefs.flatMap { ref ->
        optionsByRef[ref]?.references()?.map {
            RemoveOption(
                optionText = it.value,
                removeValue = optionsByRef[it]?.hasValue ?: false
            )
        } ?: emptyList()
    }

    private fun mapAddOptions(
        includeRefs: List<OptionRef>,
        option: Option,
        valueExtractorName: String
    ) = includeRefs.map {
        AddOption(
            optionType = OptionType.valueOf(option.optionVariants.first().type.toString()),
            optionText = it.value,
            words = null,
            delimiter = option.optionVariants.first().delimiter,
            value = if (option.isReferenced(it)) "$valueExtractorName()" else "undefined",
            unique = true
        )
    }

    private fun scriptContext(
        semantics: CommandSemanticsContext,
    ): String {
        val optionRefsToHtmlId = semantics.optionsByRef.mapKeys { it.key.value }.mapValues { identifier(it.value) }
        val idMapVarName = identifier(semantics.command, Identifier.Suffix.ID_MAP)
        val descriptionVarName = identifier(semantics.command, Identifier.Suffix.DESC)
        val description = apiTypesMapper.toCommandSyntax(semantics.command)
        val semanticVarName = identifier(semantics.command, Identifier.Suffix.SEMANTIC)
        val semantic = apiTypesMapper.toSemantic(semantics)
        return jsTemplateBuilder.initIdMap(idMapVarName, optionRefsToHtmlId) +
                jsTemplateBuilder.initSyntax(descriptionVarName, description) +
                jsTemplateBuilder.initSemantic(semanticVarName, semantic) +
                jsTemplateBuilder.addSyncListener(idMapVarName, descriptionVarName, semanticVarName)
    }

    private fun removeSubCommandHandle(subCommand: SubCommand): String =
        "window.hybrid.terminal.removeSubcommandAndRest('${subCommand.name}')"

    private fun subCommandHandle(subCommand: SubCommand): String =
        "window.hybrid.terminal.insertLastArg('${subCommand.name}')"

}