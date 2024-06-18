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
            entry("Other") {
                toggles {
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
        }
    }

    subcommand("push") {
        tabs {
            entry("Branches") {
                toggles {
                    option("--all") {
                        description("Push all branches (i.e. refs under refs/heads/); cannot be used with other <refspec>.")
                    }
                    option("--prune") {
                        description(
                            """
                               Remove remote branches that don’t have a local counterpart. For
                               example a remote branch tmp will be removed if a local branch with
                               the same name doesn’t exist any more. This also respects refspecs,
                               e.g.  git push --prune remote refs/heads/*:refs/tmp/* would make
                               sure that remote refs/tmp/foo will be removed if refs/heads/foo
                               doesn’t exist.
                            """.trimIndent()
                        )
                    }
                }
            }
            entry("Refs") {
                toggles {
                    option("--mirror") {
                        description(
                            """
                               Instead of naming each ref to push, specifies that all refs under
                               refs/ (which includes but is not limited to refs/heads/,
                               refs/remotes/, and refs/tags/) be mirrored to the remote
                               repository. Newly created local refs will be pushed to the remote
                               end, locally updated refs will be force updated on the remote end,
                               and deleted refs will be removed from the remote end. This is the
                               default if the configuration option remote.<remote>.mirror is set.
                            """.trimIndent()
                        )
                    }
                    option("-d", "--delete") {
                        description("""
                            All listed refs are deleted from the remote repository. This is the
                            same as prefixing all refs with a colon.
                        """.trimIndent())
                    }
                }
                entry("Tags") {
                    toggles {
                        option("--tags") {
                            description("All refs under refs/tags are pushed, in addition to refspecs explicitly listed on the command line.")
                        }
                        option("--follow-tags") {
                            description("""
                               Push all the refs that would be pushed without this option, and
                               also push annotated tags in refs/tags that are missing from the
                               remote but are pointing at commit-ish that are reachable from the
                               refs being pushed. This can also be specified with configuration
                               variable push.followTags. For more information, see push.followTags
                               in git-config(1).
                            """.trimIndent())
                        }
                    }
                }
            }
            entry("Force") {
                toggles {
                    option("-f", "--force") {
                        description("""
                           Usually, the command refuses to update a remote ref that is not an
                           ancestor of the local ref used to overwrite it. Also, when
                           --force-with-lease option is used, the command refuses to update a
                           remote ref whose current value does not match what is expected.
                
                           This flag disables these checks, and can cause the remote
                           repository to lose commits; use it with care.
                
                           Note that --force applies to all the refs that are pushed, hence
                           using it with push.default set to matching or with multiple push
                           destinations configured with remote.*.push may overwrite refs other
                           than the current branch (including local refs that are strictly
                           behind their remote counterpart). To force a push to only one
                           branch, use a + in front of the refspec to push (e.g git push
                           origin +master to force a push to the master branch). See the
                           <refspec>... section above for details.
                        """.trimIndent())
                    }
                }
                choice {
                    option("--force-with-lease=<refname>") {
                        description("""
                           --force-with-lease alone, without specifying the details, will
                           protect all remote refs that are going to be updated by requiring
                           their current value to be the same as the remote-tracking branch we
                           have for them.
                           
                           --force-with-lease=<refname>, without specifying the expected
                           value, will protect the named ref (alone), if it is going to be
                           updated, by requiring its current value to be the same as the
                           remote-tracking branch we have for it.
                           
                           --force-with-lease=<refname>:<expect> will protect the named ref
                           (alone), if it is going to be updated, by requiring its current
                           value to be the same as the specified value <expect> (which is
                           allowed to be different from the remote-tracking branch we have for
                           the refname, or we do not even have to have such a remote-tracking
                           branch when this form is used). If <expect> is the empty string,
                           then the named ref must not already exist.
                        """.trimIndent())
                    }
                    option("--no-force-with-lease") {
                        description("will cancel all the previous --force-with-lease on the command line.")
                    }
                }
                choice {
                    option("--force-if-includes") {
                        description("""
                           Force an update only if the tip of the remote-tracking ref has been
                           integrated locally.
                
                           This option enables a check that verifies if the tip of the
                           remote-tracking ref is reachable from one of the "reflog" entries
                           of the local branch based in it for a rewrite. The check ensures
                           that any updates from the remote have been incorporated locally by
                           rejecting the forced update if that is not the case.
                
                           If the option is passed without specifying --force-with-lease, or
                           specified along with --force-with-lease=<refname>:<expect>, it is a
                           "no-op".
                        """.trimIndent())
                    }
                    option("--no-force-if-includes") {
                        description("Specifying --no-force-if-includes disables this behavior.")
                    }
                }
            }
            entry("Other") {
                toggles {
                    option("-n", "--dry-run") { description("Do everything except actually send the updates.") }
                    option("--porcelain") {
                        description("""
                           Produce machine-readable output. The output status line for each
                           ref will be tab-separated and sent to stdout instead of stderr. The
                           full symbolic names of the refs will be given.
                        """.trimIndent())
                    }
                }
                choice {
                    option("--no-signed")
                    option("--signed=<mode>") {
                        values("true", "false", "if-asked")
                        description("""
                           GPG-sign the push request to update refs on the receiving side, to
                           allow it to be checked by the hooks and/or be logged. If false or
                           --no-signed, no signing will be attempted. If true or --signed, the
                           push will fail if the server does not support signed pushes. If set
                           to if-asked, sign if and only if the server supports signed pushes.
                           The push will also fail if the actual call to gpg --sign fails. See
                           git-receive-pack(1) for the details on the receiving end.
                        """.trimIndent())
                    }
                }
                choice {
                    option("--atomic")
                    option("--no-atomic") {
                        description("""
                           Use an atomic transaction on the remote side if available. Either
                           all refs are updated, or on error, no refs are updated. If the
                           server does not support atomic pushes the push will fail.
                        """.trimIndent())
                    }
                }
                toggles {
                    option("-o <option>", "--push-option=<option>") {
                        description("""
                           Transmit the given string to the server, which passes them to the
                           pre-receive as well as the post-receive hook. The given string must
                           not contain a NUL or LF character. When multiple
                           --push-option=<option> are given, they are all sent to the other
                           side in the order listed on the command line. When no
                           --push-option=<option> is given from the command line, the values
                           of configuration variable push.pushOption are used instead.
                        """.trimIndent())
                    }
                    option("--receive-pack=<git-receive-pack>", "--exec=<git-receive-pack>") {
                        description("""
                           Path to the git-receive-pack program on the remote end. Sometimes
                           useful when pushing to a remote repository over ssh, and you do not
                           have the program in a directory on the default ${'$'}PATH.
                        """.trimIndent())
                    }
                }

                toggles {
                    option("--repo=<repository>") {
                        description("This option is equivalent to the <repository> argument. If both are " +
                                "specified, the command-line argument takes precedence.")
                    }
                    option("-u", "--set-upstream") {
                        description("""
                           For every branch that is up to date or successfully pushed, add
                           upstream (tracking) reference, used by argument-less git-pull(1)
                           and other commands. For more information, see branch.<name>.merge
                           in git-config(1).
                        """.trimIndent())
                    }
                }
                choice {
                    option("--no-thin")
                    option("--thin") {
                        description("""
                           These options are passed to git-send-pack(1). A thin transfer
                           significantly reduces the amount of sent data when the sender and
                           receiver share many of the same objects in common. The default is --thin.
                        """.trimIndent())
                    }
                }
                toggles {
                    option("-q", "--quiet") {
                        description("""
                           Suppress all output, including the listing of updated refs, unless
                           an error occurs. Progress is not reported to the standard error
                           stream.
                        """.trimIndent())
                    }
                    option("-v", "--verbose") { description("Run verbosely.") }
                    option("--progress") {
                        description("""
                           Progress status is reported on the standard error stream by default
                           when it is attached to a terminal, unless -q is specified. This
                           flag forces progress status even if the standard error stream is
                           not directed to a terminal.
                        """.trimIndent())
                    }
                }
                choice {
                    option("--no-recurse-submodules")
                    option("--recurse-submodules=<mode>") {
                        values("check", "on-demand", "only", "no")
                        description("""
                           May be used to make sure all submodule commits used by the
                           revisions to be pushed are available on a remote-tracking branch.
                           If check is used Git will verify that all submodule commits that
                           changed in the revisions to be pushed are available on at least one
                           remote of the submodule. If any commits are missing the push will
                           be aborted and exit with non-zero status. If on-demand is used all
                           submodules that changed in the revisions to be pushed will be
                           pushed. If on-demand was not able to push all necessary revisions
                           it will also be aborted and exit with non-zero status. If only is
                           used all submodules will be pushed while the superproject is left
                           unpushed. A value of no or using --no-recurse-submodules can be
                           used to override the push.recurseSubmodules configuration variable
                           when no submodule recursion is required.
                
                           When using on-demand or only, if a submodule has a
                           "push.recurseSubmodules={on-demand,only}" or "submodule.recurse"
                           configuration, further recursion will occur. In this case, "only"
                           is treated as "on-demand".
                        """.trimIndent())
                    }
                }
                choice {
                    option("--verify") {
                        description("Toggle the pre-push hook (see githooks(5)). " +
                                "The default is --verify, giving the hook a chance to prevent the push.")
                    }
                    option("--no-verify") { description("With --no-verify, the hook is bypassed completely.") }
                }
                choice {
                    option("-4", "--ipv4") { description("Use IPv4 addresses only, ignoring IPv6 addresses.") }
                    option("-6", "--ipv6") { description("Use IPv6 addresses only, ignoring IPv4 addresses.") }
                }
            }
        }
    }

    subcommand("clean") {
        toggles {
            option("-d") {
                description("""
                   Normally, when no <pathspec> is specified, git clean will not recurse into untracked directories to avoid removing too
                   much. Specify -d to have it recurse into such directories as well. If a <pathspec> is specified, -d is irrelevant; all
                   untracked files matching the specified paths (with exceptions for nested git directories mentioned under --force) will be
                   removed.
                """.trimIndent())
            }
            option("-f", "--force") {
                description("""
                   If the Git configuration variable clean.requireForce is not set to false, git clean will refuse to delete files or
                   directories unless given -f or -i. Git will refuse to modify untracked nested git repositories (directories with a .git
                   subdirectory) unless a second -f is given.
                """.trimIndent())
            }
            option("-i", "--interactive") {
                description("Show what would be done and clean files interactively. See “Interactive mode” for details.")
            }
            option("-n", "--dry-run") { description("Don’t actually remove anything, just show what would be done.") }
            option("-q", "--quiet") {
                description("Be quiet, only report errors, but not the files that are successfully removed.")
            }
            option("-e <pattern>", "--exclude=<pattern>") {
                description("Use the given exclude pattern in addition to the standard ignore rules (see gitignore(5)).")
            }
            option("-x") {
                description("""
                   Don’t use the standard ignore rules (see gitignore(5)), but still use the ignore rules given with -e options from the
                   command line. This allows removing all untracked files, including build products. This can be used (possibly in
                   conjunction with git restore or git reset) to create a pristine working directory to test a clean build.
                """.trimIndent())
            }
            option("-X") {
                description("Remove only files ignored by Git. This may be useful to rebuild everything from scratch, but keep manually created files.")
            }
        }
    }

    subcommand("clone") {
        toggles {
            option("-l", "--local") {
                description("""
                   When the repository to clone from is on a local machine, this flag bypasses the normal "Git aware" transport mechanism
                   and clones the repository by making a copy of HEAD and everything under objects and refs directories. The files under
                   .git/objects/ directory are hardlinked to save space when possible.
        
                   If the repository is specified as a local path (e.g., /path/to/repo), this is the default, and --local is essentially a
                   no-op. If the repository is specified as a URL, then this flag is ignored (and we never use the local optimizations).
                   Specifying --no-local will override the default when /path/to/repo is given, using the regular Git transport instead.
                """.trimIndent())
            }
            option("--no-hardlinks") {
                description("Force the cloning process from a repository on a local filesystem to copy the files under the .git/objects directory " +
                        "instead of using hardlinks. This may be desirable if you are trying to make a back-up of your repository.")
            }
            option("-s", "--shared") {
                description("""
                   When the repository to clone is on the local machine, instead of using hard links, automatically setup
                   .git/objects/info/alternates to share the objects with the source repository. The resulting repository starts out without
                   any object of its own.
                """.trimIndent())
            }
            option("--dissociate") {
                description("""
                   Borrow the objects from reference repositories specified with the --reference options only to reduce network transfer,
                   and stop borrowing from them after a clone is made by making necessary local copies of borrowed objects. This option can
                   also be used when cloning locally from a repository that already borrows objects from another repository—the new
                   repository will borrow objects from the same repository, and this option can be used to stop the borrowing.
                """.trimIndent())
            }
            option("-v", "--verbose") {
                description("Run verbosely. Does not affect the reporting of progress status to the standard error stream.")
            }
            option("--server-option=<option>") {
                description("""
                   Transmit the given string to the server when communicating using protocol version 2. The given string must not contain a
                   NUL or LF character. The server’s handling of server options, including unknown ones, is server-specific. When multiple
                   --server-option=<option> are given, they are all sent to the other side in the order listed on the command line.
                """.trimIndent())
            }
            option("-n", "--no-checkout") {
                description("No checkout of HEAD is performed after the clone is complete.")
            }
            option("--bare") {
                description("""
                   Make a bare Git repository. That is, instead of creating <directory> and placing the administrative files in
                   <directory>/.git, make the <directory> itself the ${'$'}GIT_DIR. This obviously implies the --no-checkout because there is
                   nowhere to check out the working tree. Also the branch heads at the remote are copied directly to corresponding local
                   branch heads, without mapping them to refs/remotes/origin/. When this option is used, neither remote-tracking branches
                   nor the related configuration variables are created.
                """.trimIndent())
            }
            option("--sparse") {
                description("""
                    Employ a sparse-checkout, with only files in the toplevel directory initially being present. The git-sparse-checkout(1)
                    command can be used to grow the working directory as needed.
                """.trimIndent())
            }
            option("--filter=<filter-spec>") {
                description("""
                   Use the partial clone feature and request that the server sends a subset of reachable objects according to a given object
                   filter. When using --filter, the supplied <filter-spec> is used for the partial clone filter. For example,
                   --filter=blob:none will filter out all blobs (file contents) until needed by Git. Also, --filter=blob:limit=<size> will
                   filter out all blobs of size at least <size>. For more details on filter specifications, see the --filter option in git-
                   rev-list(1).
                """.trimIndent())
            }
            option("--also-filter-submodules") {
                description("""
                    Also apply the partial clone filter to any submodules in the repository. Requires --filter and --recurse-submodules. This
                    can be turned on by default by setting the clone.filterSubmodules config option.
                """.trimIndent())
            }
            option("--mirror") {
                description("""
                   Set up a mirror of the source repository. This implies --bare. Compared to --bare, --mirror not only maps local branches
                   of the source to local branches of the target, it maps all refs (including remote-tracking branches, notes etc.) and sets
                   up a refspec configuration such that all these refs are overwritten by a git remote update in the target repository.
                """.trimIndent())
            }
            option("-o <name>", "--origin <name>") {
                description("""
                    Instead of using the remote name origin to keep track of the upstream repository, use <name>. Overrides
                    clone.defaultRemoteName from the config.
                """.trimIndent())
            }
            option("-b <name>", "--branch <name>") {
                description("""
                   Instead of pointing the newly created HEAD to the branch pointed to by the cloned repository’s HEAD, point to <name>
                   branch instead. In a non-bare repository, this is the branch that will be checked out.  --branch can also take tags and
                   detaches the HEAD at that commit in the resulting repository.
                """.trimIndent())
            }
            option("-u <upload-pick>", "--upload-pack <upload-pack>") {
                description("""
                    When given, and the repository to clone from is accessed via ssh, this specifies a non-default path for the command run
                    on the other end.
                """.trimIndent())
            }
            option("--template=<template-directory>") {
                description("""
                    Specify the directory from which templates will be used; (See the "TEMPLATE DIRECTORY" section of git-init(1).)
                """.trimIndent())
            }
            option("-c <key=value>", "--config=<key=value>") {
                description("""
                   Set a configuration variable in the newly-created repository; this takes effect immediately after the repository is
                   initialized, but before the remote history is fetched or any files checked out. The key is in the same format as expected
                   by git-config(1) (e.g., core.eol=true). If multiple values are given for the same key, each value will be written to the
                   config file. This makes it safe, for example, to add additional fetch refspecs to the origin remote.
        
                   Due to limitations of the current implementation, some configuration variables do not take effect until after the initial
                   fetch and checkout. Configuration variables known to not take effect are: remote.<name>.mirror and remote.<name>.tagOpt.
                   Use the corresponding --mirror and --no-tags options instead.
                """.trimIndent())
            }
            option("--depth <depth>") {
                description("""
                   Create a shallow clone with a history truncated to the specified number of commits. Implies --single-branch unless
                   --no-single-branch is given to fetch the histories near the tips of all branches. If you want to clone submodules
                   shallowly, also pass --shallow-submodules.
                """.trimIndent())
            }
            option("--shalow-since=<date>") {
                description("Create a shallow clone with a history after the specified time.")
            }
            option("--shallow-exclude=<revision>") {
                description("Create a shallow clone with a history, excluding commits reachable from a specified remote branch or tag. This option can " +
                        "be specified multiple times.")
            }
            option("--no-tags") {
                description("""
                   Don’t clone any tags, and set remote.<remote>.tagOpt=--no-tags in the config, ensuring that future git pull and git fetch
                   operations won’t follow any tags. Subsequent explicit tag fetches will still work, (see git-fetch(1)).
        
                   Can be used in conjunction with --single-branch to clone and maintain a branch with no references other than a single
                   cloned branch. This is useful e.g. to maintain minimal clones of the default branch of some repository for search
                   indexing.
                """.trimIndent())
            }
            option("--recurse-submodules=<pathspec>") {
                description("""
                   After the clone is created, initialize and clone submodules within based on the provided pathspec. If no pathspec is
                   provided, all submodules are initialized and cloned. This option can be given multiple times for pathspecs consisting of
                   multiple entries. The resulting clone has submodule.active set to the provided pathspec, or "." (meaning all submodules)
                   if no pathspec is provided.
        
                   Submodules are initialized and cloned using their default settings. This is equivalent to running git submodule update
                   --init --recursive <pathspec> immediately after the clone is finished. This option is ignored if the cloned repository
                   does not have a worktree/checkout (i.e. if any of --no-checkout/-n, --bare, or --mirror is given)
                """.trimIndent())
            }
            option("--separate-git-dir=<git-dir>") {
                description("""
                   Instead of placing the cloned repository where it is supposed to be, place the cloned repository at the specified
                   directory, then make a filesystem-agnostic Git symbolic link to there. The result is Git repository can be separated from
                   working tree.
                """.trimIndent())
            }
            option("-j <n>", "--jobs <n>") {
                description("The number of submodules fetched at the same time. Defaults to the submodule.fetchJobs option.")
            }
            option("--bundle-uri=<uri>") {
                description("""
                   Before fetching from the remote, fetch a bundle from the given <uri> and unbundle the data into the local repository. The
                   refs in the bundle will be stored under the hidden refs/bundle/* namespace. This option is incompatible with --depth,
                   --shallow-since, and --shallow-exclude.
                """.trimIndent())
                -"--depth"
                -"--shallow-since"
                -"--shallow-exclude"
            }
        }
        choice {
            option("--no-remote-submodules")
            option("--remote-submodules") {
                description("""
                    All submodules which are cloned will use the status of the submodule’s remote-tracking branch to update the submodule,
                    rather than the superproject’s recorded SHA-1. Equivalent to passing --remote to git submodule update.
                """.trimIndent())
            }
        }
        choice {
            option("--no-shallow-modules")
            option("--shallow-modules") {
                description("All submodules which are cloned will be shallow with a depth of 1.")
            }
        }
        choice {
            option("--no-single-branch")
            option("--single-branch") {
                description("""
                   Clone only the history leading to the tip of a single branch, either specified by the --branch option or the primary
                   branch remote’s HEAD points at. Further fetches into the resulting repository will only update the remote-tracking branch
                   for the branch this option was used for the initial cloning. If the HEAD at the remote did not point at any branch when
                   --single-branch clone was made, no remote-tracking branch is created.
                """.trimIndent())
            }
        }
        choice {
            option("--no-reject-shallow")
            option("--reject-shallow") {
                description("Fail if the source repository is a shallow repository. The clone.rejectShallow configuration variable can be used to" +
                        "specify the default.")
            }
        }
        choice {
            option("-q", "--quiet") {
                description("Operate quietly. Progress is not reported to the standard error stream.")
            }
            option("--progress") {
                description("Progress status is reported on the standard error stream by default when it is attached to a terminal, unless --quiet is" +
                        "specified. This flag forces progress status even if the standard error stream is not directed to a terminal.")
            }
        }
        choice {
            option("--reference <repository>") {
                description("""
                   If the reference repository is on the local machine, automatically setup .git/objects/info/alternates to obtain objects
                   from the reference repository. Using an already existing repository as an alternate will require fewer objects to be
                   copied from the repository being cloned, reducing network and local storage costs.
                """.trimIndent())
            }
            option("--reference-if-able <repository>") {
                description("""
                    When using the --reference-if-able, a non existing directory is skipped with a warning instead of aborting the clone.
                """.trimIndent())
            }
        }
    }

    subcommand("init") {
        toggles {
            option("-q", "--quiet") {
                description("Only print error and warning messages; all other output will be suppressed.")
            }
            option("--bare") {
                description("Create a bare repository. If GIT_DIR environment is not set, it is set to the current working directory.")
            }
            option("--object-format=<format>") {
                description("Specify the given object format (hash algorithm) for the repository. The valid values are sha1 and (if enabled) sha256. " +
                        "sha1 is the default.")
            }
            option("--template=<template-directory>") {
                description("Specify the directory from which templates will be used.")
            }
            option("--separate-git-dir=<git-dir>") {
                description("""
                   Instead of initializing the repository as a directory to either ${'$'}GIT_DIR or ./.git/, create a text file there containing
                   the path to the actual repository. This file acts as filesystem-agnostic Git symbolic link to the repository.
        
                   If this is reinitialization, the repository will be moved to the specified path.
                """.trimIndent())
            }
            option("-b <branch-name>", "--initial-branch=<branch-name>") {
                description("""
                   Use the specified name for the initial branch in the newly created repository. If not specified, fall back to the default
                   name (currently master, but this is subject to change in the future; the name can be customized via the
                   init.defaultBranch configuration variable).
                """.trimIndent())
            }
            option("--shared=<value>") {
                description("""
                    Specify that the Git repository is to be shared amongst several users. This allows users belonging to the same group to
                   push into that repository. When specified, the config variable "core.sharedRepository" is set so that files and
                   directories under ${'$'}GIT_DIR are created with the requested permissions. When not specified, Git will use permissions
                   reported by umask(2).
                """.trimIndent())
            }
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