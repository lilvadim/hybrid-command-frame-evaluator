# Hybrid Command Frame Evaluator

Консольная утилита, которая генерирует HTML command frame для HybridTerm app. JS-скрипты в генерируемых фреймах используют Hybrid API, который предоставляет HybridTerm через объект `window.hybrid`.

Инструмент принимает на вход описание команды на языке KotlinScript с использованием DSL:
```kotlin
import ru.nsu.hybrid.dsl.api.* 
```
Описание позволяет задать структуру отображения опций и их семантику.

Пример описания и использования операторов
```kotlin
complexCommand("git") { // составная команда git
    subcommand("commit") { // подкоманда commit
        entry("Sample Entry") { // раздел с подразделами в виде заголовков
            choice { // опции на выбор
                option("-o <arg>", "--option <arg>") { // варианты написания опции
                    description("Option description") // описание опции
                    values("VALUE1", "VALUE2") // перечисляемые значения

                    exclude("--option-to-toggle-on") // выключить при включении этой опции
                    include("option1", "option2") // включить при включении этой опции
                    +"--option-to-toggle-on" // включить при включении этой опции, альтернативный вариант
                    -"--option-to-toggle-off" // выключить при включении этой опции, альтернативный вариант
                    toggle on "--option-to-toggle-on" // включить при включении этой опции, альтернативный вариант
                    toggle off options("--option1", "option2") // выключить при включении этой опции, альтернативный вариант

                    exclusiveInGroups("all-toggle-off-group") // выключить все другие опции в группе при включении этой
                    inclusiveInGroups("group1", "group2") // включить все другие опции в группе при включении этой
                    exclusive in "all-toggle-off-in-group" // выключить все другие опции в группе, альтернативный вариант
                    inclusive in groups("group1", "group2") // включить все другие опции в группе при включении этой, альтернативный вариант
                }
                option("-s") // самый краткий вариант описания опции
            }
            tabs("Name is optional on tabs") { // раздел с подразделами в виде вкладок, наименование можно опустить и использовать tabs { ... }
                entry("Sample Tab Entry") { 
                    toggles { // перечисление переключателей опций
                        option("-s", "--sample-opt") {
                            description("Sample option")
                        }
                    }
                }
            }
        }
    }
}
```
Для верстки фреймов используются компоненты Bootstrap 5, отображение будет корректным если подключить Bootstrap CSS. 
Также, для работы вкладок требуется просто подключить Bootstrap JS Plugin.