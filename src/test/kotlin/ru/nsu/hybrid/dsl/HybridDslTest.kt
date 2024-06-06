package ru.nsu.hybrid.dsl

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import ru.nsu.hybrid.cf.commandDesc.entry.Command
import ru.nsu.hybrid.dsl.api.alternate
import ru.nsu.hybrid.dsl.api.complexCommand
import ru.nsu.hybrid.dsl.api.simpleCommand
import kotlin.test.Test

class HybridDslTest {

    private val objectMapper: ObjectMapper = ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT)

    @Test
    fun sample_simpleCommand() {
        val lsCommand: Command = simpleCommand("ls") {
            entry("Filter") {
                choice {
                    option("-a") { description("Show hidden files") }
                    option("-A") { description("Do not ignore entries starting with '.', except '.' and '..'") }
                }
                toggles {
                    option("-d") {
                        description("List directory entries instead of directory contents. " +
                                "Do not dereference symbolic links")
                    }
                    option("-R") { description("Recurse into subdirectories") }
                }
            }
            entry("Format") {
                toggles {
                    option("--author")
                    option("--block-size=<unit>") {
                        values("K", "M", "G", "T", "KB", "MB", "GB", "TB")
                        description("Units to use")
                    }
                    option("-i") { description("For each file, write the file's file serial number") }
                    option("-s") { description("Indicate the total number of file system blocks consumed by each file displayed") }
                    option("-k") { description("Assume block size of 1024 bytes") }
                    option("-H") {
                        description("Evaluate the file information and file type for symbolic links specified on the command " +
                                "line to be those of the file referenced by the link, and not the link itself; " +
                                "however, ls shall " +
                                "write the name of the link itself and not the file referenced by the link ")
                    }
                    option("-L")
                }
                choice {
                    option("-C") { description("Multicolumn output (short entries)") }
                    option("-m") { description("Comma-separated list of short entries") }
                }
            }
            entry("Sort") {
                toggles {
                    option("-S") {
                        description("Sort with the primary key being file size (in decreasing order) and the secondary " +
                                "key being filename in the collating sequence (in increasing order)")
                    }
                    option("-t") {
                        description("Sort with the primary key being time modified (most recently modified first) " +
                                "and the secondary key being filename in the collating sequence")
                    }
                    option("-f") {
                        description("List the entries in directory operands in the order they appear in the directory. " +
                                "Filter option -a will be turned on automatically. Sort option -r will be ignored")
                        exclusive in "foo"
                        toggle on "-a"
                    }
                    option("-r") {
                        description("Reverse the order of the sort to get reverse collating sequence oldest first, " +
                                "or smallest file size first depending on the other options given")
                    }
                    option("-w<width>") {
                        description("Set output width to the number of columns. 0 means no limit")
                        exclusive in "foo"
                    }
                }
            }
        }
        printJson(lsCommand)
    }

    private fun printJson(lsCommand: Command) {
        println(objectMapper.writeValueAsString(lsCommand))
    }

    @Test
    fun sample_complexCommand() {
        complexCommand("git") {
            tabs(setType = alternate) {

            }
            subcommand("commit") {
                entry("Sample Tab Entry") {
                    choice {
                        option("-o <arg>", "--option <arg>") {
                            description("Option description")
                            values("VALUE1", "VALUE2")

                            exclude("--option-to-toggle-on")
                            include("option1", "option2")
                            +"--option-to-toggle-on"
                            -"--option-to-toggle-off"
                            toggle on "--option-to-toggle-on"
                            toggle off options("--option1", "option2")

                            exclusiveInGroups("all-toggle-off-group")
                            inclusiveInGroups("group1", "group2")
                            exclusive in "all-toggle-off-in-group"
                            inclusive in groups("group1", "group2")
                        }
                        option("-s")
                    }
                }
                tabs("Name is optional on tabs", alternate) {
                    entry("Sample Tab Entry") {
                        toggles {
                            option("-s", "--sample-opt") {
                                description("Sample option")
                            }
                        }
                    }
                }
            }
        }.let { printJson(it) }
    }
}