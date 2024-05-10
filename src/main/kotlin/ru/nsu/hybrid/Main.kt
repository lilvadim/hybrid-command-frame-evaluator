package ru.nsu.hybrid

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import ru.nsu.hybrid.facade.CommandFrameEvaluatorFacade
import java.io.File

fun main(args: Array<String>) {
    val argParser = ArgParser("hybrid")

    val sourcePath: String by argParser.argument(ArgType.String, "source", "*.kts description file")
    val outPath: String by argParser.argument(ArgType.String, "out", "*.html output file")

    argParser.parse(args)

    val sourceFile = File(sourcePath)
    val outFile = File(outPath).also {
        if (!it.exists()) {
            it.createNewFile()
        }
    }

    val commandFrameEvaluatorFacade = CommandFrameEvaluatorFacade()
    commandFrameEvaluatorFacade.evaluateHtmlCommandFrame(sourceFile, outFile)
}