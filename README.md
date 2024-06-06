# Hybrid Command Frame Evaluator

A console utility that generates an HTML command frame for the HybridTerm app. The JavaScript scripts in the generated frames use the Hybrid API, provided by HybridTerm through the `window.hybrid` object.

The tool takes a command description in KotlinScript using DSL:
```kotlin
import ru.nsu.hybrid.dsl.api.*
```
Description allows to describe layout of displaying toggles as well as semantics of them.

Example of command description and usage of operators:
```kotlin
complexCommand("git") { // composite command git
    subcommand("commit") { // subcommand commit
        entry("Sample Entry") { // section with subsections as headers
            choice { // options to choose from
                option("-o <arg>", "--option <arg>") { // option aliases
                    description("Option description") // option description
                    values("VALUE1", "VALUE2") // enumerated values

                    exclude("--option-to-toggle-on") // disable when this option is enabled
                    include("option1", "option2") // enable when this option is enabled
                    +"--option-to-toggle-on" // enable when this option is enabled, alternative variant
                    -"--option-to-toggle-off" // disable when this option is enabled, alternative variant
                    toggle on "--option-to-toggle-on" // enable when this option is enabled, alternative variant
                    toggle off options("--option1", "option2") // disable when this option is enabled, alternative variant

                    exclusiveInGroups("all-toggle-off-group") // disable all other options in the group when this one is enabled
                    inclusiveInGroups("group1", "group2") // enable all other options in the group when this one is enabled
                    exclusive in "all-toggle-off-in-group" // disable all other options in the group, alternative variant
                    inclusive in groups("group1", "group2") // enable all other options in the group when this one is enabled, alternative variant
                }
                option("-s") // shortest option description
            }
        }
    }
}
```
For the layout of the frames, Bootstrap 5 components are used. The display will be correct if you include Bootstrap CSS.
Additionally, for the tabs to work, it is enough to just activate the Bootstrap JS Plugin.