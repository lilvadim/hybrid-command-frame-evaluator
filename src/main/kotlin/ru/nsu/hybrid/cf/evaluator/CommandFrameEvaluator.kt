package ru.nsu.hybrid.cf.evaluator

import kotlinx.html.*
import kotlinx.html.dom.createHTMLDocument
import org.w3c.dom.Document
import ru.nsu.hybrid.cf.commandDesc.entry.*
import ru.nsu.hybrid.cf.commandDesc.option.ChoiceOptionSet
import ru.nsu.hybrid.cf.commandDesc.option.Option
import ru.nsu.hybrid.cf.commandDesc.option.OptionSet
import ru.nsu.hybrid.cf.commandDesc.option.ToggleOptionSet
import ru.nsu.hybrid.cf.evaluator.action.ActionDescriptor
import ru.nsu.hybrid.cf.evaluator.action.CommandFrameActionsEvaluator

class CommandFrameEvaluator(
    val actionsEvaluator: CommandFrameActionsEvaluator = CommandFrameActionsEvaluator()
) {
    val actionMap = mutableMapOf<ActionDescriptor, String>()

    fun evaluate(command: Command): Document {
        val scriptContext = when (command) {
            is SimpleCommand -> {
                val (script, map) = actionsEvaluator.evaluate(command)
                actionMap.putAll(map)
                script
            }
            else -> null
        }
        return createHTMLDocument().html {
            lang = "javascript"
            head {
            }
            body {
                script {
                    unsafe {
                        raw("$scriptContext")
                    }
                }
                when (command) {
                    is SimpleCommand -> evaluate(command)
                    is ComplexCommand -> evaluate(command)
                }
            }
        }
    }

    fun FlowContent.evaluate(complexCommand: ComplexCommand) {
        div("container") {
            id = htmlId(complexCommand)
            h2("mono-font-bold") { +complexCommand.name }
            complexCommand.subcommands?.let {
                if (it.isNotEmpty()) {
                    h3 { +"Subcommands" }
                }
            }
            div("list-group") {
                complexCommand.subcommands?.forEach { subcommand ->
                    button(classes = "list-group-item") {
                        p("mono-font-bold") { +subcommand.name }
                    }
                }
            }
            complexCommand.options?.let {
                if (it.isNotEmpty()) {
                    h3 { +"Options" }
                }
            }
            complexCommand.options?.forEach { optionSet -> div("list-group-item") { evaluate(optionSet) } }
            complexCommand.entries?.forEach { subEntry -> evaluate(subEntry) }
        }
    }

    fun FlowContent.evaluate(simpleCommand: SimpleCommand) {
        div {
            h2("mono-font-bold") { +simpleCommand.name }
            simpleCommand.options?.forEach { optionSet -> div("list-group-item") { evaluate(optionSet) } }
            simpleCommand.entries?.forEach { evaluate(it) }
        }
    }

    fun FlowContent.evaluate(subEntry: SubEntry) {
        when (subEntry) {
            is InlineEntry -> evaluate(subEntry)
            is TabEntry -> evaluate(subEntry)
        }
    }

    fun FlowContent.evaluate(entry: TabEntry) {
        button(classes = "nav-link") {

        }
    }

    fun FlowContent.evaluate(entry: InlineEntry) {
        div {
            h3 { +entry.name }
            div("list-group list-group-flush") {
                entry.options?.forEach { optionSet -> div("list-group-item") { evaluate(optionSet) } }
                entry.entries?.forEach { inlineEntry -> div("list-group-item") { evaluate(inlineEntry) } }
            }
        }
    }

    fun FlowContent.evaluate(optionSet: OptionSet) {
        when (optionSet) {
            is ToggleOptionSet -> evaluate(optionSet)
            is ChoiceOptionSet -> evaluate(optionSet)
        }
    }

    fun FlowContent.evaluate(optionSet: ToggleOptionSet) {
        div("list-group") {
            optionSet.forEach { option ->
                div("list-group-item") {
                    div("form-check") {
                        label("form-check-label mono-font-bold") {
                            +htmlLabel(option)
                            input(InputType.checkBox, classes = "form-check-input") {
                                value = ""
                            }
                            onClick = actionMap[ActionDescriptor(htmlId(option))] ?: ""
                        }
                    }
                    option.description?.let { text(it) }
                }
            }
        }
    }

    fun FlowContent.evaluate(optionSet: ChoiceOptionSet) {
        div("list-group") {
            optionSet.forEach { option ->
                div("list-group-item") {
                    div("form-check") {
                        label("form-check-label mono-font-bold") {
                            htmlFor = htmlId(option)
                            +htmlLabel(option)
                            input(
                                InputType.radio,
                                classes = "form-check-input",
                                name = htmlId(optionSet)
                            ) {
                                value = ""
                            }
                        }
                    }
                    option.description?.let { text(it) }
                }
            }
        }
    }

    private fun htmlLabel(option: Option): String = option.optionVariants.joinToString(separator = ", ") { it.value }

}

