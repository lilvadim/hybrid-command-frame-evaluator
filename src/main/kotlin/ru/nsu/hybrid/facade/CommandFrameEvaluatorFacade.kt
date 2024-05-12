package ru.nsu.hybrid.facade

import kotlinx.html.dom.serialize
import ru.nsu.hybrid.cf.evaluator.CommandFrameEvaluator
import ru.nsu.hybrid.dsl.evaluator.CommandDescriptionEvaluator
import java.io.File

class CommandFrameEvaluatorFacade(
    private val commandDescriptionEvaluator: CommandDescriptionEvaluator = CommandDescriptionEvaluator(),
    private val commandFrameEvaluator: CommandFrameEvaluator = CommandFrameEvaluator(),
) {
    fun evaluateHtmlCommandFrame(source: File, outFile: File?): String {
        val commandObject = commandDescriptionEvaluator.evaluateFromFile(source)
        val commandFrame = commandFrameEvaluator.evaluate(commandObject)

        val html = commandFrame.serialize()
        outFile?.writeText(html)

        return html
    }
}