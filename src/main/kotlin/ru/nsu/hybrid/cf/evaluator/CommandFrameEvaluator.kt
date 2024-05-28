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
    private val actionsEvaluator: CommandFrameActionsEvaluator = CommandFrameActionsEvaluator()
) {
    private val actionMap = mutableMapOf<ActionDescriptor, String>()

    fun evaluate(command: Command): Document {
        return createHTMLDocument().html {
            lang = "javascript"
            head { }
            body {
                script { +initActionsAndGetScriptContext(command) }
                when (command) {
                    is ComplexCommand -> {
                        div {
                            evaluate(command)
                            command.subcommands?.forEach { evaluate(it) }
                        }
                    }
                    is SimpleCommand -> evaluate(command)
                }
            }
        }
    }

    private fun initActionsAndGetScriptContext(command: Command): String {
        val actions = actionsEvaluator.commandFrameActions(command)
        actionMap.putAll(actions.actionMap)
        return actions.scriptContext
    }

    private fun FlowContent.evaluate(complexCommand: ComplexCommand) {
        div("container") {
            id = identifier(complexCommand)
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
                        onClick = actionMap[ActionDescriptor(identifier(subcommand))] ?: ""
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

    private fun FlowContent.evaluate(simpleCommand: SimpleCommand) {
        div {
            id = identifier(simpleCommand)
            if (simpleCommand is SubCommand) {
                classes += "invisible"
            }

            h2("mono-font-bold") {
                if (simpleCommand is SubCommand) {
                    a(href = "#") {
                        +simpleCommand.parentCommandName
                        onClick = actionMap[ActionDescriptor(commandIdentifier(
                            simpleCommand.parentCommandName, simpleCommand.name
                        ))] ?: ""
                    }
                }
                +" "
                +simpleCommand.name
            }
            simpleCommand.options?.forEach { optionSet -> div("list-group-item") { evaluate(optionSet) } }
            simpleCommand.entries?.forEach { evaluate(it) }
        }
    }

    private fun FlowContent.evaluate(subEntry: SubEntry) {
        when (subEntry) {
            is InlineEntry -> evaluate(subEntry)
            is TabEntry -> evaluate(subEntry)
        }
    }

    private fun FlowContent.evaluate(tabEntry: TabEntry) {
        div {
            h3 { +tabEntry.name }
            nav {
                div("nav nav-underline") {
                    role = "tablist"
                    tabEntry.entries?.forEach { subEntry ->
                        button(classes = "nav-link tab-btn", type = ButtonType.button) {
                            +subEntry.name
                            id = identifier(subEntry, "tab")
                            role = "tab"
                            attributes["data-bs-toggle"] = "tab"
                            attributes["data-bs-target"] = "#" + identifier(subEntry, "tab_pane")
                            attributes["aria-controls"] = identifier(subEntry, "tab_pane")
                            attributes["aria-selected"] = "false"
                        }
                    }
                }
            }
            div("tab-content") {
                tabEntry.entries?.forEach { subEntry ->
                    div("tab-pane list-group list-group-flush") {
                        id = identifier(subEntry, "tab_pane")
                        role = "tabpanel"
                        attributes["aria-labelledby"] = identifier(subEntry, "tab")
                        tabIndex = "0"
                        subEntry.options?.forEach { optionSet -> div("list-group-item") { evaluate(optionSet) } }
                        subEntry.entries?.forEach { s -> div("list-group-item") { evaluate(s) } }
                    }
                }
            }
        }
    }

    private fun FlowContent.evaluate(entry: InlineEntry) {
        div {
            h3 { +entry.name }
            div("list-group list-group-flush") {
                entry.options?.forEach { optionSet -> div("list-group-item") { evaluate(optionSet) } }
                entry.entries?.forEach { inlineEntry -> div("list-group-item") { evaluate(inlineEntry) } }
            }
        }
    }

    private fun FlowContent.evaluate(optionSet: OptionSet) {
        when (optionSet) {
            is ToggleOptionSet -> evaluate(optionSet)
            is ChoiceOptionSet -> evaluate(optionSet)
        }
    }

    private fun FlowContent.evaluate(optionSet: ToggleOptionSet) {
        div("list-group list-group-flush") {
            optionSet.forEach { option ->
                div("list-group-item") {
                    evaluate(option, optionSet)
                    option.description?.let { text(it) }
                }
            }
        }
    }

    private fun FlowContent.evaluate(option: Option, optionSet: OptionSet) {
        val customClass = if (optionSet is ChoiceOptionSet) "radio-check" else ""
        when {
            option.hasValue && option.values.isNullOrEmpty() -> div("input-group") {
                div("input-group-text") {
                    div("form-check") {
                        label("form-check-label mono-font-bold") {
                            +htmlLabel(option)
                            input(InputType.checkBox, classes = "form-check-input $customClass") {
                                name = identifier(optionSet)
                                id = identifier(option)
                                value = ""
                            }
                            onClick = actionMap[ActionDescriptor(identifier(option))] ?: ""
                        }
                    }
                }
                input(InputType.text, classes = "form-control") {
                    id = identifier(option, Identifier.Suffix.VALUE)
                    placeholder = option.optionVariants.first().arg
                }
            }
            option.hasValue -> div("input-group") {
                div("input-group-text") {
                    div("form-check") {
                        label("form-check-label mono-font-bold") {
                            +htmlLabel(option)
                            input(InputType.checkBox, classes = "form-check-input $customClass") {
                                name = identifier(optionSet)
                                id = identifier(option)
                                value = ""
                            }
                            onClick = actionMap[ActionDescriptor(identifier(option))] ?: ""
                        }
                    }
                }
                select("form-select") {
                    id = identifier(option, Identifier.Suffix.VALUE)
                    option {
                        selected = true
                        +""
                    }
                    option.values?.forEach { optValue ->
                        option {
                            value = optValue
                            +optValue
                        }
                    }
                }
            }
            else -> div("form-check") {
                label("form-check-label mono-font-bold") {
                    htmlFor = identifier(option)
                    +htmlLabel(option)
                    input(InputType.checkBox, classes = "form-check-input $customClass") {
                        name = identifier(optionSet)
                        id = identifier(option)
                        value = ""
                    }
                    onClick = actionMap[ActionDescriptor(identifier(option))] ?: ""
                }
            }
        }
    }

    private fun FlowContent.evaluate(optionSet: ChoiceOptionSet) {
        div("list-group") {
            optionSet.forEach { option ->
                div("list-group-item") {
                    evaluate(option, optionSet)
                    option.description?.let { text(it) }
                }
            }
        }
    }

    private fun htmlLabel(option: Option): String = option.optionVariants.joinToString(separator = ", ") { it.pattern }

}

