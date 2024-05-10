package ru.nsu.hybrid.cf.evaluator.action

import kotlinx.html.Entities
import ru.nsu.hybrid.cf.commandDesc.entry.SimpleCommand
import ru.nsu.hybrid.cf.commandDesc.entry.SubCommand
import ru.nsu.hybrid.cf.commandDesc.semantics.SemanticsAnalyzer
import ru.nsu.hybrid.cf.evaluator.htmlId
import ru.nsu.hybrid.cf.evaluator.types.CommandDescriptor
import ru.nsu.hybrid.cf.evaluator.types.CommandOption
import ru.nsu.hybrid.cf.evaluator.types.mapper.TypesMapper


class CommandFrameActionsEvaluator(
    val semanticsAnalyzer: SemanticsAnalyzer = SemanticsAnalyzer()
) {

    val apiReference = "window.hybrid"
    val terminalApi = "$apiReference.terminal"

    val jsBuilder: JsBuilder = JsBuilder()
    val typesMapper: TypesMapper = TypesMapper.instance

    fun evaluate(
        command: SimpleCommand
    ): CommandFrameActions {
        val semantics = semanticsAnalyzer.commandSemantics(command)
        var scriptContext = jsBuilder.call(terminalApi, "registerCommand") {
            typesMapper.toCommandDescription(command)
        } + "\n\n"
        val actionMap: MutableMap<ActionDescriptor, String> = mutableMapOf()
        for (optionSet in semantics.flattenOptionSets) {
            for (option in optionSet) {
                val (_, include, exclude) = semantics.optionSemantics(option.references().first())
                val addOptions = include.map {
                    CommandOption(
                        option = it.value,
                        index = null,
                        value = null
                    )
                }
                val removeOptions = exclude.map {
                    CommandOption(
                        option = it.value,
                        index = null,
                        value = null
                    )
                }
                val commandDescriptor = if (command is SubCommand) CommandDescriptor(
                    command = command.parentCommandName,
                    subcommand = command.name
                ) else CommandDescriptor(
                    command = command.name,
                    subcommand = null
                )
                val handlerName = htmlId(option) + "_handler"
                scriptContext += jsBuilder.function(handlerName) {
                    if (addOptions.isNotEmpty()) {
                        call(terminalApi, "addOptions") {
                            object {
                                val commandDescriptor = commandDescriptor
                                val options = addOptions
                            }
                        }
                    }
                    if (removeOptions.isNotEmpty()) {
                        call(terminalApi, "removeOptions") {
                            object {
                                val commandDescriptor = commandDescriptor
                                val options = removeOptions
                            }
                        }
                    }
                }
                actionMap[ActionDescriptor(htmlId(option))] = jsBuilder.call(handlerName)
            }
        }

        return CommandFrameActions(
            scriptContext = escapeHtmlEntities(scriptContext),
            actionMap = actionMap.mapValues { escapeHtmlEntities(it.value) }
        )
    }

    private fun escapeHtmlEntities(value: String): String {
        return value.replace("<", Entities.lt.text).replace(">", Entities.gt.text)
    }

}