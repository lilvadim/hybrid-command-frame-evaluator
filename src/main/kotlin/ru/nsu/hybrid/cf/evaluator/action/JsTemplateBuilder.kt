package ru.nsu.hybrid.cf.evaluator.action

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import ru.nsu.hybrid.cf.evaluator.Identifier
import ru.nsu.hybrid.cf.evaluator.action.apiTypes.CommandSemantic
import ru.nsu.hybrid.cf.evaluator.action.apiTypes.CommandSyntax
import ru.nsu.hybrid.cf.evaluator.action.apiTypes.UpdateOptionsParameters

class JsTemplateBuilder(
    private val objectMapper: ObjectMapper = jacksonObjectMapper()
) {
    fun addSyncListener(
        idMapVarName: String,
        syntaxVarName: String,
        semanticVarName: String,
    ): String = """
        window.hybrid.commandInfo.addSyntax($syntaxVarName);
        window.hybrid.commandInfo.addSemantic($semanticVarName);
        window.hybrid.terminal.onCommandLineSync((e) => {
            if (!e.commandLine) {
                return;
            }
            if (e.commandLine.command.command !== $syntaxVarName.command) {
                return;
            }
            
            var options = e.commandLine.command.options;
            Object.values($idMapVarName).forEach((id) => window.hybrid.uiUtils.toggleOff(id));
            options.forEach((opt) => {
                if (opt.option.type === 'UNIX' && opt.option.words) {
                    opt.option.words?.forEach((w) => {
                        window.hybrid.uiUtils.toggleOn($idMapVarName[opt.option.prefix + w]);
                        if (opt.value) {
                            window.hybrid.uiUtils.setValue(
                                $idMapVarName[opt.option.prefix + w] + '_${Identifier.Suffix.VALUE}',
                                opt.value
                            );
                        }
                    });
                } else {
                    window.hybrid.uiUtils.toggleOn($idMapVarName[opt.option.option]);
                    if (opt.value) {
                        window.hybrid.uiUtils.setValue(
                            $idMapVarName[opt.option.option] + '_${Identifier.Suffix.VALUE}', 
                            opt.value
                        );
                    }
                }
            });
            
            var syntaxHelper = window.hybrid.getCommandSyntaxHelper($syntaxVarName);
            if (!syntaxHelper) {
                return;
            }
            var subcommand = syntaxHelper.getSubcommand(e.commandLine.command);
            var commandFrameId = '${Identifier.Prefix.COMMAND}' + e.commandLine.command.command;
            if (!subcommand) {
                window.hybrid.uiUtils.show(commandFrameId);
                Object.values($syntaxVarName.subcommands).forEach((sc) => 
                    window.hybrid.uiUtils.hide('${Identifier.Prefix.COMMAND}' + e.commandLine.command.command + '_' + sc));
            } else {
                var subcommandFrameId = '${Identifier.Prefix.COMMAND}' + e.commandLine.command.command + '_' + subcommand;
                window.hybrid.uiUtils.hide(commandFrameId);
                window.hybrid.uiUtils.show(subcommandFrameId);
            }
            
            if (!e.oldCommandLine) {
                return;
            }
            var oldOptionsTextArr = e.oldCommandLine.command.options.map((it) => it.option.option);
            var addedOptions = 
                e.commandLine.command.options.filter((it) => !oldOptionsTextArr.includes(it.option.option));
            if (!addedOptions.length) { 
                return; 
            }
            var lastAddedOption = addedOptions[addedOptions.length - 1];
            var lastAddedOptionInputId = $idMapVarName[lastAddedOption.option.option];
            var tabPane = document.getElementById(lastAddedOptionInputId)?.closest('div[role="tabpanel"]');
            if (!tabPane) { 
                return;
            }
            var tabId = tabPane.getAttribute('aria-labelledby');
            window.hybrid.uiUtils.showTab(tabId);
        });
    """.trimIndent() + "\n"

    fun addSubcommandSyncListener(
        idMapVarName: String,
        syntaxVarName: String,
        semanticVarName: String,
        parentSyntaxVarName: String
    ): String = """
        window.hybrid.commandInfo.addSyntax($syntaxVarName);
        window.hybrid.commandInfo.addSemantic($semanticVarName);
        window.hybrid.terminal.onCommandLineSync((e) => {
            if (!e.commandLine) {
                return;
            }
            var syntaxHelper = window.hybrid.getCommandSyntaxHelper($parentSyntaxVarName);
            if (!syntaxHelper) {
                return;
            }
            var subcommand = syntaxHelper.getSubcommand(e.commandLine.command);
            var commandName = subcommand ? e.commandLine.command.command + ' ' + subcommand : e.commandLine.command.command;
            if (commandName !== $syntaxVarName.command) {
                return;
            }
            
            var options = e.commandLine.command.options;
            Object.values($idMapVarName).forEach((id) => window.hybrid.uiUtils.toggleOff(id));
            options.forEach((opt) => {
                if (opt.option.type === 'UNIX' && opt.option.words) {
                    opt.option.words?.forEach((w) => {
                        window.hybrid.uiUtils.toggleOn($idMapVarName[opt.option.prefix + w]);
                        if (opt.value) {
                            window.hybrid.uiUtils.setValue(
                                $idMapVarName[opt.option.prefix + w] + '_${Identifier.Suffix.VALUE}',
                                opt.value
                            );
                        }
                    });
                } else {
                    window.hybrid.uiUtils.toggleOn($idMapVarName[opt.option.option]);
                    if (opt.value) {
                        window.hybrid.uiUtils.setValue(
                            $idMapVarName[opt.option.option] + '_${Identifier.Suffix.VALUE}', 
                            opt.value
                        );
                    }
                }
            });
            
            if (!e.oldCommandLine) {
                return;
            }
            var oldOptionsTextArr = e.oldCommandLine.command.options.map((it) => it.option.option);
            var addedOptions = 
                e.commandLine.command.options.filter((it) => !oldOptionsTextArr.includes(it.option.option));
            if (!addedOptions.length) { 
                return; 
            }
            var lastAddedOption = addedOptions[addedOptions.length - 1];
            var lastAddedOptionInputId = $idMapVarName[lastAddedOption.option.option];
            var tabPane = document.getElementById(lastAddedOptionInputId)?.closest('div[role="tabpanel"]');
            if (!tabPane) { 
                return;
            }
            var tabId = tabPane.getAttribute('aria-labelledby');
            window.hybrid.uiUtils.showTab(tabId);
        });
    """.trimIndent() + "\n"

    fun initIdMap(
        idMapVarName: String,
        optionRefsToHtmlId: Map<String, String>
    ): String = """
        var $idMapVarName = ${objectMapper.writeValueAsString(optionRefsToHtmlId)};
    """.trimIndent() + "\n"

    fun initSyntax(
        syntaxVarName: String,
        commandSyntax: CommandSyntax
    ): String = """
        var $syntaxVarName = ${objectMapper.writeValueAsString(commandSyntax)};
    """.trimIndent() + "\n"

    fun initSemantic(
        semanticVarName: String,
        semantic: CommandSemantic
    ): String = """
        var $semanticVarName = ${objectMapper.writeValueAsString(semantic)};
    """.trimIndent() + "\n"

    fun handlerDef(
        handlerName: String,
        optionId: String,
        updateOptionsWhenToggleOn: UpdateOptionsParameters,
        updateOptionsWhenToggleOff: UpdateOptionsParameters,
        valueExtractorName: String,
        inputId: String
    ) = """
        function $handlerName() {
            var data = document.getElementById('${optionId}').checked;
            if (data) {
                window.hybrid.terminal.updateOptions(${objectMapper.writeValueAsString(updateOptionsWhenToggleOn)});
            } else {
                window.hybrid.terminal.updateOptions(${objectMapper.writeValueAsString(updateOptionsWhenToggleOff)});
            }
        }
        function $valueExtractorName() {
            var elem = document.getElementById('${inputId}');
            if (elem) {
                return elem.value;
            }
            return undefined;
        }
    """.trimIndent() + "\n"

}