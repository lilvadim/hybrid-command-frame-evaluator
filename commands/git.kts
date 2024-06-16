import ru.nsu.hybrid.dsl.api.*

complexCommand("git") {
    subcommand("commit") {
        tabs {
            entry("Log") {
                choice {
                    option("-m <msg>", "--message=<msg>") {
                        description("""
                            Use the given <msg> as the commit message. If multiple -m options are given, their values are concatenated as
                            separate paragraphs.
        
                            The -m option is mutually exclusive with -c, -C, and -F.
                        """.trimIndent())
                    }
                    option("-C <commit>", "--reuse-message=<commit>") {
                        description("""
                            Take an existing commit object, and reuse the log message and the authorship information (including the timestamp)
                            when creating the commit.
                        """.trimIndent())
                    }
                    option("-c <commit>", "--reedit-message=<commit>") {
                        description("Like -C, but with -c the editor is invoked, so that the user can further edit the commit message.")
                    }
                }
                toggles {
                    option("--squash=<commit>") {
                        description("""
                           Construct a commit message for use with rebase --autosquash. The commit message subject line is taken from the
                           specified commit with a prefix of "squash! ". Can be used with additional commit message options (-m/-c/-C/-F). See
                           git-rebase(1) for details.
                        """.trimIndent())
                    }
                    option("--reset-author") {
                        description("""
                            When used with -C/-c/--amend options, or when committing after a conflicting cherry-pick, declare that the authorship
                            of the resulting commit now belongs to the committer. This also renews the author timestamp.
                        """.trimIndent())
                    }
                    option("-F <file>", "--file=<file>") {
                        description("Take the commit message from the given file. Use - to read the message from the standard input.")
                    }
                    option("-t <file>", "--template=<file>") {
                        description("""
                           When editing the commit message, start the editor with the contents in the given file. The commit.template
                           configuration variable is often used to give this option implicitly to the command. This mechanism can be used by
                           projects that want to guide participants with some hints on what to write in the message in what order. If the user
                           exits the editor without editing the message, the commit is aborted. This has no effect when a message is given by
                           other means, e.g. with the -m or -F options.
                        """.trimIndent())
                    }
                    option("--author=<author>") {
                        description("""
                           Override the commit author. Specify an explicit author using the standard A U Thor <author@example.com> format.
                           Otherwise <author> is assumed to be a pattern and is used to search for an existing commit by that author (i.e.
                           rev-list --all -i --author=<author>); the commit author is then copied from the first such commit found.
                        """.trimIndent())
                    }
                    option("--date=<date>") { description("Override the author date used in the commit.") }
                    option("-s", "--signoff") {
                        description("""
                           Add a Signed-off-by trailer by the committer at the end of the commit log message. The meaning of a signoff depends
                           on the project to which you’re committing. For example, it may certify that the committer has the rights to submit
                           the work under the project’s license or agrees to some contributor representation, such as a Developer Certificate of
                           Origin. (See http://developercertificate.org for the one used by the Linux kernel and Git projects.) Consult the
                           documentation or leadership of the project to which you’re contributing to understand how the signoffs are used in
                           that project.
                        """.trimIndent())
                    }
                    option("--no-signoff") {
                        description("The --no-signoff option can be used to countermand an earlier --signoff option on the command line.")
                    }
                    option("-n", "--no-verify") {
                        description("""
                            By default, the pre-commit and commit-msg hooks are run. When any of --no-verify or -n is given, these are bypassed.
                            See also githooks(5).
                        """.trimIndent())
                    }
                    option("--allow-empty") {
                        description("""
                            Like --allow-empty this command is primarily for use by foreign SCM interface scripts. It allows you to create a
                            commit with an empty commit message without using plumbing commands like git-commit-tree(1).
                        """.trimIndent())
                    }
                    option("--cleanup-mode=<mode>") {
                        description("""
                           This option determines how the supplied commit message should be cleaned up before committing. The <mode> can be
                           strip, whitespace, verbatim, scissors or default.
        
                           strip: 
                               Strip leading and trailing empty lines, trailing whitespace, commentary and collapse consecutive empty lines.
        
                           whitespace: 
                               Same as strip except #commentary is not removed.
        
                           verbatim: 
                               Do not change the message at all.
        
                           scissors: 
                               Same as whitespace except that everything from (and including) the line found below is truncated, if the message
                               is to be edited. "#" can be customized with core.commentChar.
        
                                   # ------------------------ >8 ------------------------
        
                           default:
                               Same as strip if the message is to be edited. Otherwise whitespace.
        
                           The default can be changed by the commit.cleanup configuration variable (see git-config(1)).
                        """.trimIndent())
                        values("strip", "whitespace", "verbatim", "scissors", "default")
                    }
                    option("-e", "--edit") {
                        description("""
                            The message taken from file with -F, command line with -m, and from commit object with -C are usually used as the
                            commit log message unmodified. This option lets you further edit the message taken from these sources.
                        """.trimIndent())
                    }
                    option("--no-edit") {
                        description("""
                            Use the selected commit message without launching an editor. For example, git commit --amend --no-edit amends a
                            commit without changing its commit message.
                        """.trimIndent())
                    }
                    option("--status") {
                        description("""
                            Include the output of git-status(1) in the commit message template when using an editor to prepare the commit
                            message. Defaults to on, but can be used to override configuration variable commit.status.
                        """.trimIndent())
                    }
                    option("--no-status") {
                        description("""
                            Do not include the output of git-status(1) in the commit message template when using an editor to prepare the default
                            commit message.
                        """.trimIndent())
                    }
                }
            }
            entry("Changes") {
                toggles {
                    option("-a", "--all") {
                        description("""
                            Tell the command to automatically stage files that have been modified and deleted, but new files you have not told
                            Git about are not affected.
                        """.trimIndent())
                    }
                    option("-p", "--patch") {
                        description("Use the interactive patch selection interface to choose which changes to commit. See git-add(1) for details.")
                    }
                    option("-u<mode>", "--untracked-files=<mode>") {
                        description("""
                           Show untracked files.
        
                           The mode parameter is optional (defaults to all), and is used to specify the handling of untracked files; when -u is
                           not used, the default is normal, i.e. show untracked files and directories.
                
                           The possible options are:
                
                           •   no - Show no untracked files
                
                           •   normal - Shows untracked files and directories
                
                           •   all - Also shows individual files in untracked directories.
                
                           The default can be changed using the status.showUntrackedFiles configuration variable documented in git-config(1).
                        """.trimIndent())
                        values("no", "normal", "all")
                    }
                    option("-i", "--include") {
                        description("""
                            Before making a commit out of staged contents so far, stage the contents of paths given on the command line as well.
                            This is usually not what you want unless you are concluding a conflicted merge.
                        """.trimIndent())
                    }
                    option("-o", "--only") {
                        description("""
                           Make a commit by taking the updated working tree contents of the paths specified on the command line, disregarding
                           any contents that have been staged for other paths. This is the default mode of operation of git commit if any paths
                           are given on the command line, in which case this option can be omitted. If this option is specified together with
                           --amend, then no paths need to be specified, which can be used to amend the last commit without committing changes
                           that have already been staged. If used together with --allow-empty paths are also not required, and an empty commit
                           will be created.
                        """.trimIndent())
                    }
                    option("-v", "--verbose") {
                        description("""
                           Show unified diff between the HEAD commit and what would be committed at the bottom of the commit message template to
                           help the user describe the commit by reminding what changes the commit has. Note that this diff output doesn’t have
                           its lines prefixed with #. This diff will not be a part of the commit message. See the commit.verbose configuration
                           variable in git-config(1).
                
                           If specified twice, show in addition the unified diff between what would be committed and the worktree files, i.e.
                           the unstaged changes to tracked files.
                        """.trimIndent())
                    }
                }
            }
            entry("Dry run") {
                toggles {
                    option("--dry-run") {
                        description("""
                            Do not create a commit, but show a list of paths that are to be committed, paths with local changes that will be left
                            uncommitted and paths that are untracked.
                        """.trimIndent())
                    }
                    option("--short") {
                        description("When doing a dry-run, give the output in the short-format. See git-status(1) for details. Implies --dry-run.")
                        toggle on "--dry-run"
                    }
                    option("--branch") { description("Show the branch and tracking info even in short-format.") }
                    option("--porcelain") {
                        description("When doing a dry-run, give the output in a porcelain-ready format. See git-status(1) for details. Implies --dry-run.")
                        toggle on "--dry-run"
                    }
                    option("--long") {
                        description("When doing a dry-run, give the output in the long-format. Implies --dry-run.")
                        toggle on "--dry-run"
                    }
                    option("-z", "--null") {
                        description("""
                           When showing short or porcelain status output, print the filename verbatim and terminate the entries with NUL,
                           instead of LF. If no format is given, implies the --porcelain output format. Without the -z option, filenames with
                           "unusual" characters are quoted as explained for the configuration variable core.quotePath (see git-config(1)).
                        """.trimIndent())
                    }
                }
            }
        }

        toggles {
            option("--amend") {
                description("""
                   Replace the tip of the current branch by creating a new commit. The recorded tree is prepared as usual (including the
                   effect of the -i and -o options and explicit pathspec), and the message from the original commit is used as the
                   starting point, instead of an empty message, when no other message is specified from the command line via options
                   such as -m, -F, -c, etc. The new commit has the same parents and author as the current one (the --reset-author option
                   can countermand this).
        
                   You should understand the implications of rewriting history if you amend a commit that has already been published.
                   (See the "RECOVERING FROM UPSTREAM REBASE" section in git-rebase(1).)
                """.trimIndent())
            }
            option("--no-post-rewrite") { description("Bypass the post-rewrite hook.") }
            option("--pathspec-from-file=<file>") {
                description("""
                   Pathspec is passed in <file> instead of commandline args. If <file> is exactly - then standard input is used.
                   Pathspec elements are separated by LF or CR/LF. Pathspec elements can be quoted as explained for the configuration
                   variable core.quotePath (see git-config(1)). See also --pathspec-file-nul and global --literal-pathspecs.
                """.trimIndent())
            }
            option("--pathspec-file-nul") {
                description("""
                    Only meaningful with --pathspec-from-file. Pathspec elements are separated with NUL character and all other
                    characters are taken literally (including newlines and quotes).
                """.trimIndent())
                +"--pathspec-from-file"
            }

            option("-q", "--quiet") { description("Suppress commit summary message.") }
            option("-S<keyid>", "--gpg-sign=<keyid>") {
                description("""
                    GPG-sign commits. The keyid argument is optional and defaults to the committer identity; if specified, it must be
                    stuck to the option without a space.
                """.trimIndent())
            }
            option("--no-gpg-sign") {
                description("--no-gpg-sign is useful to countermand both commit.gpgSign configuration variable, and earlier --gpg-sign.")
            }
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
    tabs {
        entry("Help") {
            toggles {
                option("-h", "--help") {
                    description("""
                       Prints the synopsis and a list of the most commonly used commands. If the option --all or -a is given then all
                       available commands are printed. If a Git command is named this option will bring up the manual page for that command.
            
                       Other options are available to control how the manual page is displayed. See git-help(1) for more information,
                       because git --help ... is converted internally into git help ....
                    """.trimIndent())
                    toggle off "-v"
                }
                option("-v", "--version") {
                    description("""
                       Prints the Git suite version that the git program came from.
            
                       This option is internally converted to git version ... and accepts the same options as the git-version(1) command. If
                       --help is also given, it takes precedence over --version.
                    """.trimIndent())
                }
            }
        }
        entry("Print path") {
            toggles {
                option("--html-path") {
                    description("Print the path, without trailing slash, where Git’s HTML documentation is installed and exit.")
                }
                option("--man-path") {
                    description("Print the manpath (see man(1)) for the man pages for this version of Git and exit.")
                }
                option("--info-path") {
                    description("Print the path where the Info files documenting this version of Git are installed and exit.")
                }
            }
        }
        entry("Path specs processing") {
            toggles {
                option("--literal-pathspecs") {
                    description("""
                        Treat pathspecs literally (i.e. no globbing, no pathspec magic). This is equivalent to setting the
                        GIT_LITERAL_PATHSPECS environment variable to 1.
                    """.trimIndent())
                }
                option("--noglob-pathspecs") {
                    description("""
                        Add "literal" magic to all pathspec. This is equivalent to setting the GIT_NOGLOB_PATHSPECS environment variable to
                        1. Enabling globbing on individual pathspecs can be done using pathspec magic ":(glob)"
                    """.trimIndent())
                }
                option("--icase-pathspecs") {
                    description("""
                        Add "icase" magic to all pathspec. This is equivalent to setting the GIT_ICASE_PATHSPECS environment variable to 1.
                    """.trimIndent())
                }
            }
        }
        entry("Other") {
            toggles {
                option("-C <path>") {
                    description("""
                       Run as if git was started in <path> instead of the current working directory. When multiple -C options are given,
                       each subsequent non-absolute -C <path> is interpreted relative to the preceding -C <path>. If <path> is present but
                       empty, e.g.  -C "", then the current working directory is left unchanged.
                       
                       This option affects options that expect path name like --git-dir and --work-tree in that their interpretations of the
                       path names would be made relative to the working directory caused by the -C option.
                    """.trimIndent())
                }
                option("--exec-path=<path>") {
                    description("""
                        Path to wherever your core Git programs are installed. This can also be controlled by setting the GIT_EXEC_PATH
                        environment variable. If no path is given, git will print the current setting and then exit.
                    """.trimIndent())
                }

                option("-p", "--paginate") {
                    description("""
                        Pipe all output into less (or if set, ${'$'}PAGER) if standard output is a terminal. This overrides the pager.<cmd>
                        configuration options (see the "Configuration Mechanism" section below).
                    """.trimIndent())
                }
                option("-P", "--no-pager") {
                    description("Do not pipe Git output into a pager.")
                }
                option("--git-dir=<path>") {
                    description("""
                       Set the path to the repository (".git" directory). This can also be controlled by setting the GIT_DIR environment
                       variable. It can be an absolute path or relative path to current working directory.
            
                       Specifying the location of the ".git" directory using this option (or GIT_DIR environment variable) turns off the
                       repository discovery that tries to find a directory with ".git" subdirectory (which is how the repository and the
                       top-level of the working tree are discovered), and tells Git that you are at the top level of the working tree. If
                       you are not at the top-level directory of the working tree, you should tell Git where the top-level of the working
                       tree is, with the --work-tree=<path> option (or GIT_WORK_TREE environment variable)
            
                       If you just want to run git as if it was started in <path> then use git -C <path>.
                    """.trimIndent())
                }
                option("--work-tree=<path>") {
                    description("""
                       Set the path to the working tree. It can be an absolute path or a path relative to the current working directory.
                       This can also be controlled by setting the GIT_WORK_TREE environment variable and the core.worktree configuration
                       variable (see core.worktree in git-config(1) for a more detailed discussion).
                    """.trimIndent())
                }
                option("--namespace=<path>") {
                    description("""
                        Set the Git namespace. See gitnamespaces(7) for more details. Equivalent to setting the GIT_NAMESPACE environment
                        variable.
                    """.trimIndent())
                }
                option("--bare") {
                    description("""
                        Treat the repository as a bare repository. If GIT_DIR environment is not set, it is set to the current working
                        directory.
                    """.trimIndent())
                }
                option("--no-replace-objects") {
                    description("Do not use replacement refs to replace Git objects. See git-replace(1) for more information.")
                }

                option("--no-optional-locks") {
                    description("""
                        Do not perform optional operations that require locks. This is equivalent to setting the GIT_OPTIONAL_LOCKS to 0.
                    """.trimIndent())
                }
                option("--list-cmds=<groups>") {
                    description("""
                       List commands by group. This is an internal/experimental option and may change or be removed in the future. Supported
                       groups are: builtins, parseopt (builtin commands that use parse-options), main (all commands in libexec directory),
                       others (all other commands in ${'$'}PATH that have git- prefix), list-<category> (see categories in command-list.txt),
                       nohelpers (exclude helper commands), alias and config (retrieve command list from config variable
                       completion.commands)
                    """.trimIndent())
                }
            }
        }
    }
}