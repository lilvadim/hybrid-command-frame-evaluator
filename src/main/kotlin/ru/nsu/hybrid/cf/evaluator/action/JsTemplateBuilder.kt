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
        semanticVarName: String
    ): String = """
        window.hybrid.commandInfo.addSyntax($syntaxVarName);
        window.hybrid.commandInfo.addSemantic($semanticVarName);
        window.hybrid.terminal.onCommandLineSync((e) => {
            if (!e.commandLine || e.commandLine.command.command !== $syntaxVarName.command) {
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
            
            var syntaxHelper = window.hybrid.getCommandSyntaxHelper(e.commandLine.command.command);
            if (!syntaxHelper) {
                return;
            }
            var subcommand = syntaxHelper.getSubcommand(e.commandLine.command);
            var commandFrameId = '${Identifier.Prefix.COMMAND}' + e.commandLine.command.command;
            if (!subcommand) {
                window.hybrid.uiUtils.show(commandFrameId);
                Object.values($syntaxVarName.subcommands).forEach((sc) => 
                    window.hybrid.uiUtils.hide('${Identifier.Prefix.COMMAND}' + sc));
            } else {
                var subcommandFrameId = '${Identifier.Prefix.COMMAND}' + subcommand;
                window.hybrid.uiUtils.hide(commandFrameId);
                window.hybrid.uiUtils.show(subcommandFrameId);
            }
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