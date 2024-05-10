package ru.nsu.hybrid.cf.evaluator

import kotlinx.html.dom.serialize
import org.junit.jupiter.api.Test
import ru.nsu.hybrid.dsl.builder.simpleCommand

class CommandFrameEvaluatorTest {

    val uut = CommandFrameEvaluator()

    @Test
    fun evaluate() {

        val ls = simpleCommand("ls") {
            entry("filter") {
                choice {
                    option("-a")
                    option("-A")
                }
                toggles {
                    option("-d")
                    option("-R") { description("Recurse into subdirectories") }
                }
            }
            entry("format") {
                toggles {
                    option("--author")
                    option("--block-size=<unit>") {
                        values("K", "M", "G", "T", "KB", "MB", "GB", "TB")
                        description("Units to use")
                    }
                    option("-i")
                    option("-s")
                    option("-k")
                    option("-H")
                    option("-L")
                }
                choice {
                    option("-C")
                    option("-m")

                }
            }
            entry("sort") {
                toggles {
                    option("-S")
                    option("-t")
                    option("-f")
                    option("-r")
                }
            }
        }

        val document = uut.evaluate(ls)

        print(document.serialize())
    }
}