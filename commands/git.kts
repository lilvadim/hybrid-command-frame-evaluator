import ru.nsu.hybrid.dsl.builder.*

complexCommand("git") {
    subcommand("commit") {
        toggles {
            option("-m<msg>", "--message<msg>")
            option("--amend")
        }
    }

    subcommand("push") {
        toggles {
            option("-f", "--force")
        }
    }

    subcommand("rebase") {
        toggles {
            option("-i")
        }
    }
    toggles {
        option("-h", "--help")
    }
}