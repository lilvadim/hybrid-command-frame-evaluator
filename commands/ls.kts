import ru.nsu.hybrid.dsl.api.*

simpleCommand("ls") {
    tabs {
        entry("Filter") {
            choice {
                option("-a") { description("Show hidden files") }
                option("-A") { description("Do not ignore entries starting with '.', except '.' and '..'") }
            }
            toggles {
                option("-d") {
                    description(
                        "List directory entries instead of directory contents. " +
                                "Do not dereference symbolic links"
                    )
                }
                option("-R") { description("Recurse into subdirectories") }
            }
        }
        entry("Format") {
            toggles {
                option("--author") { description("With -l, print the author of each file") }
                option("--block-size=<unit>") {
                    values("K", "M", "G", "T", "KB", "MB", "GB", "TB")
                    description("Units to use")
                }
                option("-i") { description("For each file, write the file's file serial number") }
                option("-s") { description("Indicate the total number of file system blocks consumed by each file displayed") }
                option("-k") { description("Assume block size of 1024 bytes") }
                option("-H") {
                    description(
                        "Evaluate the file information and file type for symbolic links specified on the command " +
                                "line to be those of the file referenced by the link, and not the link itself; " +
                                "however, ls shall " +
                                "write the name of the link itself and not the file referenced by the link "
                    )
                }
                option("-L")
            }
            tabs("Output") {
                entry("Short") { toggles {
                    option("-C") {
                        description("Multicolumn output (short entries)")
                        exclusive in groups("output1", "output2")
                    }
                } }
                entry("List") { toggles {
                    option("-m") {
                        description("Comma-separated list of short entries")
                        exclusive in groups("output1", "output2")
                    }
                } }
                entry("Long") {
                    choice {
                        option("-l") {
                            description("""
                            (The letter ell.) Write out in long format.
                                Do not follow symbolic links named as operands unless the -H or -L 
                                options are specified
                                """)
                            exclusive in groups("output1")
                        }
                        option("-g") {
                            description("Same as -l but do not list owners name/number")
                            exclusive in groups("output1")
                        }
                        option("-n") {
                            description("Same as -l but use numeric UID/GID")
                            exclusive in groups("output1")
                        }
                        option("-o") {
                            description("Same as -l but disable group info")
                            exclusive in groups("output1")
                        }
                    }
                    toggles {
                        option("-c") {
                            description("Use the time of last modification of the file status information instead of last modification of the file itself")
                            exclusive in groups("output2")
                        }
                    }
                }
            }
        }
        entry("Sort") {
            toggles {
                option("-S") {
                    description(
                        "Sort with the primary key being file size (in decreasing order) and the secondary " +
                                "key being filename in the collating sequence (in increasing order)"
                    )
                }
                option("-t") {
                    description(
                        "Sort with the primary key being time modified (most recently modified first) " +
                                "and the secondary key being filename in the collating sequence"
                    )
                }
                option("-f") {
                    description(
                        "List the entries in directory operands in the order they appear in the directory. " +
                                "Filter option -a will be turned on automatically. Sort option -r will be ignored"
                    )
                    exclusive in "foo"
                    toggle on "-a"
                }
                option("-r") {
                    description(
                        "Reverse the order of the sort to get reverse collating sequence oldest first, " +
                                "or smallest file size first depending on the other options given"
                    )
                    exclusive in "foo"
                }
                option("-w<width>") {
                    description("Set output width to the number of columns. 0 means no limit")
                }
            }
        }
    }
}