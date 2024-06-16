package ru.nsu.hybrid.cf.evaluator

import kotlinx.html.*
import kotlinx.html.dom.createHTMLDocument
import org.w3c.dom.Document
import ru.nsu.hybrid.cf.commandDesc.SetType
import ru.nsu.hybrid.cf.commandDesc.entry.*
import ru.nsu.hybrid.cf.commandDesc.option.Option
import ru.nsu.hybrid.cf.commandDesc.option.OptionSet
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
                script {
                    +initActionsAndGetScriptContext(command)
                }
                style {
                    unsafe {
                        raw("""
                            .black-check:checked {
                                background-color: var(--bs-dark);
                            }
                        """.trimIndent())
                    }
                }
                when (command) {
                    is ComplexCommand -> {
                        div {
                            evaluateComplexCommand(command)
                            command.subcommands?.forEach { evaluateSimpleCommand(it) }
                        }
                    }
                    is SimpleCommand -> evaluateSimpleCommand(command)
                }
            }
        }
    }

    private fun initActionsAndGetScriptContext(command: Command): String {
        val actions = actionsEvaluator.commandFrameActions(command)
        actionMap.putAll(actions.actionMap)
        var scriptContext = actions.scriptContext
        if (command is ComplexCommand) {
            command.subcommands?.forEach {
                val subcommandActions = actionsEvaluator.commandFrameActions(it)
                actionMap.putAll(subcommandActions.actionMap)
                scriptContext += subcommandActions.scriptContext
            }
        }
        return scriptContext
    }

    private fun FlowContent.evaluateComplexCommand(complexCommand: ComplexCommand) {
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
                        onClick = actionMap[ActionDescriptor(identifier(subcommand, Identifier.Suffix.SHOW))] ?: ""
                    }
                }
            }
            complexCommand.options?.let {
                if (it.isNotEmpty()) {
                    h3 { +"Options" }
                }
            }
            complexCommand.options?.forEach { optionSet -> div("list-group-item") { evaluateOptionSet(optionSet) } }
            complexCommand.entries?.forEach { subEntry -> evaluateSubEntry(subEntry) }
        }
    }

    private fun FlowContent.evaluateSimpleCommand(simpleCommand: SimpleCommand) {
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
                            simpleCommand.parentCommandName + "_" + simpleCommand.name,
                            Identifier.Suffix.HIDE
                        ))] ?: ""
                    }
                }
                +" "
                +simpleCommand.name
            }
            simpleCommand.options?.forEach { optionSet -> div("list-group-item") { evaluateOptionSet(optionSet) } }
            simpleCommand.entries?.forEach { evaluateSubEntry(it) }
        }
    }

    private fun FlowContent.evaluateSubEntry(subEntry: SubEntry) {
        when (subEntry.childrenLayout) {
            ChildrenLayout.INLINE -> evaluateInline(subEntry)
            ChildrenLayout.TABS -> evaluateTabs(subEntry)
        }
    }

    private fun FlowContent.evaluateTabs(entry: SubEntry) {
        div {
            h3 { +entry.name }
            nav {
                div("nav nav-underline") {
                    role = "tablist"
                    entry.entries?.forEachIndexed { index, subEntry ->
                        button(classes = "nav-link tab-btn", type = ButtonType.button) {
                            +subEntry.name
                            id = identifier(subEntry, Identifier.Suffix.TAB)
                            role = "tab"
                            attributes["data-bs-toggle"] = "tab"
                            attributes["data-bs-target"] = "#" + identifier(subEntry, Identifier.Suffix.TAB_PANE)
                            attributes["aria-controls"] = identifier(subEntry, Identifier.Suffix.TAB_PANE)
                            attributes["aria-selected"] = if (index == 0) "true" else "false"
                            if (index == 0) {
                                classes += "active"
                            }
                        }
                    }
                }
            }
            div("tab-content") {
                entry.entries?.forEachIndexed { index, subEntry ->
                    div("tab-pane list-group list-group-flush") {
                        id = identifier(subEntry, Identifier.Suffix.TAB_PANE)
                        role = "tabpanel"
                        attributes["aria-labelledby"] = identifier(subEntry, Identifier.Suffix.TAB)
                        tabIndex = "0"
                        subEntry.options?.forEach { optionSet -> div("list-group-item") { evaluateOptionSet(optionSet) } }
                        subEntry.entries?.forEach { s -> div("list-group-item") { evaluateSubEntry(s) } }
                        if (index == 0) {
                            classes += setOf("show", "active")
                        }
                    }
                }
            }
        }
    }

    private fun FlowContent.evaluateInline(entry: SubEntry) {
        div {
            h3 { +entry.name }
            div("list-group list-group-flush") {
                entry.options?.forEach { optionSet -> div("list-group-item") { evaluateOptionSet(optionSet) } }
                entry.entries?.forEach { subEntry -> evaluateSubEntry(subEntry) }
            }
        }
    }

    private fun FlowContent.evaluateOptionSet(optionSet: OptionSet) {
        when (optionSet.setType) {
            SetType.ANY -> evaluateAny(optionSet)
            SetType.ALTERNATE -> evaluateAlternate(optionSet)
        }
    }

    private fun FlowContent.evaluateAny(optionSet: OptionSet) {
        div("list-group list-group-flush") {
            optionSet.forEach { option ->
                div("list-group-item border-0") {
                    evaluateOption(option, optionSet)
                    option.description?.let { text(it) }
                }
            }
        }
    }

    private fun FlowContent.evaluateOption(option: Option, optionSet: OptionSet) {
        val customClass = if (optionSet.setType == SetType.ALTERNATE) "radio-check" else ""
        when {
            option.hasValue && option.values.isNullOrEmpty() -> div("input-group") {
                div("input-group-text") {
                    div("form-check") {
                        label("form-check-label mono-font-bold") {
                            +htmlLabel(option)
                            input(InputType.checkBox, classes = "form-check-input border-dark black-check $customClass") {
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
                    onChange = actionMap[ActionDescriptor(identifier(option))] ?: ""
                }
            }
            option.hasValue -> div("input-group") {
                div("input-group-text") {
                    div("form-check") {
                        label("form-check-label mono-font-bold") {
                            +htmlLabel(option)
                            input(InputType.checkBox, classes = "form-check-input border-dark black-check $customClass") {
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
                    onChange = actionMap[ActionDescriptor(identifier(option))] ?: ""
                }
            }
            else -> div("form-check") {
                label("form-check-label mono-font-bold") {
                    htmlFor = identifier(option)
                    +htmlLabel(option)
                    input(InputType.checkBox, classes = "form-check-input border-dark black-check $customClass") {
                        name = identifier(optionSet)
                        id = identifier(option)
                        value = ""
                    }
                    onClick = actionMap[ActionDescriptor(identifier(option))] ?: ""
                }
            }
        }
    }

    private fun FlowContent.evaluateAlternate(optionSet: OptionSet) {
        div("list-group") {
            optionSet.forEach { option ->
                div("list-group-item " +
                            "border-danger border-3 border-start border-end-0 border-top-0 border-bottom-0 rounded-0") {
                    evaluateOption(option, optionSet)
                    option.description?.let { text(it) }
                }
            }
        }
    }

    private fun htmlLabel(option: Option): String = option.optionVariants.joinToString(separator = ", ") { it.pattern }

}

