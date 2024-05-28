package ru.nsu.hybrid.cf.evaluator.action

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import ru.nsu.hybrid.cf.evaluator.Identifier
import ru.nsu.hybrid.cf.evaluator.action.apiTypes.CommandDescription
import ru.nsu.hybrid.cf.evaluator.action.apiTypes.UpdateOptionsParameters

class JsTemplateBuilder(
    private val objectMapper: ObjectMapper = jacksonObjectMapper()
) {
    fun addSyncListener(
        idMapVarName: String,
        descriptionVarName: String,
    ): String = """
        window.hybrid.terminal.onCommandLineSync((e) => {
            if (!e.commandLine) {
                return;
            }
            var options = e.commandLine.command.options;
            Object.values($idMapVarName).forEach((id) => {
                var input = document.getElementById(id);
                window.hybrid.utils.toggleOff(input);
            });
            options.forEach((opt) => {
                if (opt.option.type === 'UNIX' && opt.option.words) {
                    opt.option.words?.forEach((w) => {
                        var input = document.getElementById($idMapVarName[opt.option.prefix + w]);
                        window.hybrid.utils.toggleOn(input);
                        if (opt.value) {
                            var inputValue = document.getElementById($idMapVarName[opt.option.prefix + w] + '_${Identifier.Suffix.VALUE}');
                            window.hybrid.utils.setValue(inputValue, opt.value);
                        }
                    });
                } else {
                    var input = document.getElementById($idMapVarName[opt.option.option]);
                    window.hybrid.utils.toggleOn(input);
                    if (opt.value) {
                        var inputValue = document.getElementById($idMapVarName[opt.option.option] + '_${Identifier.Suffix.VALUE}');
                        window.hybrid.utils.setValue(inputValue, opt.value);
                    }
                }
            });
            
            var util = window.hybrid.getCommandLineUtil($descriptionVarName);
            var subcommand = util.getSubcommand(e.commandLine.command);
            var commandFrameId = '${Identifier.Prefix.COMMAND}' + e.commandLine.command.command;
            if (!subcommand) {
                window.hybrid.utils.show(commandFrameId);
                Object.values($descriptionVarName.subcommands).forEach((sc) => {
                    window.hybrid.utils.hide('${Identifier.Prefix.COMMAND}' + sc);
                });
            } else {
                var subcommandFrameId = '${Identifier.Prefix.COMMAND}' + subcommand;
                window.hybrid.utils.hide(commandFrameId);
                window.hybrid.utils.show(subcommandFrameId);
            }
        });
    """.trimIndent() + "\n"

    fun initIdMap(
        idMapVarName: String,
        optionRefsToHtmlId: Map<String, String>
    ): String = """
        var $idMapVarName = ${objectMapper.writeValueAsString(optionRefsToHtmlId)};
    """.trimIndent() + "\n"

    fun initDescription(
        descriptionVarName: String,
        description: CommandDescription
    ): String = """
        var $descriptionVarName = ${objectMapper.writeValueAsString(description)};
    """.trimIndent() + "\n"

    fun handlerDef(
        handlerName: String,
        optionId: String,
        updateOptionsWhenToggleOn: UpdateOptionsParameters,
        updateOptionsWhenToggleOff: UpdateOptionsParameters,
        valueExtractorName: String,
        inputId: String
    ) = """
        function $handlerName(event) {
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