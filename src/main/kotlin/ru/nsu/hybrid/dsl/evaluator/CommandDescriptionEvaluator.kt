package ru.nsu.hybrid.dsl.evaluator

import ru.nsu.hybrid.cf.commandDesc.entry.Command
import java.io.File
import javax.script.ScriptEngineManager
import javax.script.ScriptException
import kotlin.system.exitProcess

class CommandDescriptionEvaluator {

    fun evaluateFromFile(file: File): Command {
        val extension = file.extension
        if (extension != "kts") {
            throw IllegalArgumentException("${file.name}: Only *.kts files are supported")
        }
        val engine = ScriptEngineManager().getEngineByExtension(extension)
        return try { engine.eval(file.reader()) as Command } catch (e: ScriptException) {
            System.err.println("""
                ${file.name}: Error at line ${e.lineNumber}
                ${e.message}
            """.trimIndent())
            exitProcess(1)
        }
    }

}