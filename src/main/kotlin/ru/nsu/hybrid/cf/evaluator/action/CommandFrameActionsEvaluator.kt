package ru.nsu.hybrid.cf.evaluator.action

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.html.Entities
import ru.nsu.hybrid.cf.commandDesc.entry.SimpleCommand
import ru.nsu.hybrid.cf.commandDesc.entry.SubCommand
import ru.nsu.hybrid.cf.commandDesc.option.Option
import ru.nsu.hybrid.cf.commandDesc.semantics.OptionSemantics
import ru.nsu.hybrid.cf.commandDesc.semantics.SemanticsAnalyzer
import ru.nsu.hybrid.cf.commandDesc.semantics.SimpleCommandSemanticsContext
import ru.nsu.hybrid.cf.evaluator.HtmlIdSuffix
import ru.nsu.hybrid.cf.evaluator.htmlId
import ru.nsu.hybrid.cf.evaluator.types.CommandContext
import ru.nsu.hybrid.cf.evaluator.types.CommandDescriptor
import ru.nsu.hybrid.cf.evaluator.types.CommandOption
import ru.nsu.hybrid.cf.evaluator.types.UpdateOptionsParameters
import ru.nsu.hybrid.cf.evaluator.types.mapper.TypesMapper


class CommandFrameActionsEvaluator(
    private val semanticsAnalyzer: SemanticsAnalyzer = SemanticsAnalyzer(),
    private val objectMapper: ObjectMapper = ObjectMapper(),
    private val typesMapper: TypesMapper = TypesMapper.instance
) {

    fun evaluate(
        command: SimpleCommand
    ): CommandFrameActions {
        val semantics = semanticsAnalyzer.commandSemantics(command)
        var scriptContext = scriptContext(semantics)
        val actionMap: MutableMap<ActionDescriptor, String> = mutableMapOf()
        for (optionSet in semantics.flattenOptionSets) {
            for (option in optionSet) {
                val optionSemantics = semantics.optionSemantics(option.references().first())
                val (handler, handlerDef) = handler(semantics, optionSemantics, option)
                scriptContext += handlerDef
                actionMap[ActionDescriptor(htmlId(option))] = "$handler()"
            }
        }

        return CommandFrameActions(
            scriptContext = htmlEscaped(scriptContext),
            actionMap = actionMap.mapValues { htmlEscaped(it.value) }
        )
    }

    private fun handler(
        commandSemantics: SimpleCommandSemanticsContext,
        optionSemantics: OptionSemantics,
        option: Option,
    ): Pair<String, String> {
        val optionRefToPattern = commandSemantics.flattenOptionSets.flatten().associate {
            option.references().first() to option.optionVariants.map { it.pattern }.first()
        }
        val valueExtractorName = htmlId(option, HtmlIdSuffix.VALUE)
        val addOptionsWhenToggleOn = optionSemantics.whenToggleOn.include.map {
            val value = if (option.isReferenced(it)) "$valueExtractorName()" else null
            CommandOption(option = optionRefToPattern[it] ?: it.value, index = null, value = value)
        }
        val removeOptionsWhenToggleOn = optionSemantics.whenToggleOn.exclude.map {
            val value = if (option.isReferenced(it)) "$valueExtractorName()" else null
            CommandOption(option = optionRefToPattern[it] ?: it.value, index = null, value = value)
        }
        val addOptionsWhenToggleOff = optionSemantics.whenToggleOff.include.map {
            val value = if (option.isReferenced(it)) "$valueExtractorName()" else null
            CommandOption(option = optionRefToPattern[it] ?: it.value, index = null, value = value)
        }
        val removeOptionsWhenToggleOff = optionSemantics.whenToggleOff.exclude.map {
            val value = if (option.isReferenced(it)) "$valueExtractorName()" else null
            CommandOption(option = optionRefToPattern[it] ?: it.value, index = null, value = value)
        }
        val handlerName = htmlId(option, HtmlIdSuffix.HANDLER)
        val updateOptionsWhenToggleOn = UpdateOptionsParameters(
            addOptions = addOptionsWhenToggleOn,
            removeOptions = removeOptionsWhenToggleOn
        )
        val updateOptionsWhenToggleOff = UpdateOptionsParameters(
            addOptions = addOptionsWhenToggleOff,
            removeOptions = removeOptionsWhenToggleOff
        )
        val script = """
            function $handlerName() {
                var data = document.getElementById('${htmlId(option)}').checked;
                if (data) {
                    window.hybrid.terminal.updateOptions(${objectMapper.writeValueAsString(updateOptionsWhenToggleOn)});
                } else {
                    window.hybrid.terminal.updateOptions(${objectMapper.writeValueAsString(updateOptionsWhenToggleOff)});
                }
            }
            function $valueExtractorName() {
                var elem = document.getElementById('${htmlId(option, HtmlIdSuffix.VALUE)}');
                if (elem) {
                    return elem.value;
                }
                return null;
            }
        """.trimIndent() + "\n"
        return Pair(handlerName, script)
    }

    private fun scriptContext(
        semantics: SimpleCommandSemanticsContext,
    ): String {
        val command = semantics.command
        val optionRefToHtmlId = semantics.flattenOptionSets.flatten().associate { o ->
                o.optionVariants.map { it.pattern }.first() to htmlId(o)
            }
        val commandDescriptor = if (command is SubCommand) CommandDescriptor(
            command = command.parentCommandName,
            subcommand = command.name
        ) else CommandDescriptor(
            command = command.name,
            subcommand = null
        )
        val commandContext = CommandContext(commandDescriptor)
        return """
            window.hybrid.terminal.registerCommand(${objectMapper.writeValueAsString(typesMapper.toCommandDescription(command))});
            var optionToId = ${objectMapper.writeValueAsString(optionRefToHtmlId)};
            window.hybrid.terminal.setCommandContext(${objectMapper.writeValueAsString(commandContext)});
            window.hybrid.terminal.onCommandLineSync((event) => {
                Object.values(optionToId).forEach((id) => {
                    var elem = document.getElementById(id);
                    if (elem) {
                        elem.checked = false;
                    }
                });
                event.command.options.forEach((option) => {
                    var elem = document.getElementById(optionToId[option.option]);
                    if (elem) {
                        elem.checked = true;
                    }
                    var elemValue = document.getElementById(optionToId[option.option] + '${HtmlIdSuffix.VALUE}');
                    if (elemValue) {
                        elemValue.value = option.value;
                    }
                });
            });
        """.trimIndent() + "\n"
    }
}

fun htmlEscaped(value: String): String {
    return value.replace("<", Entities.lt.text).replace(">", Entities.gt.text)
}