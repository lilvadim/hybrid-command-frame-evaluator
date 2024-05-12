package ru.nsu.hybrid.dsl.evaluator

import ru.nsu.hybrid.cf.commandDesc.entry.Command
import java.io.File
import javax.script.ScriptEngineManager

class CommandDescriptionEvaluator {

    fun evaluateFromFile(file: File): Command {
        val extension = file.extension
        if (extension != "kts") {
            throw IllegalArgumentException("${file.name}: Only *.kts files are supported")
        }
        val engine = ScriptEngineManager().getEngineByExtension(extension)
        return engine.eval(file.readText()) as Command
    }

}