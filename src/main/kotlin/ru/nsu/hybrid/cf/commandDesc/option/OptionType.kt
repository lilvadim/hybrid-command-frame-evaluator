package ru.nsu.hybrid.cf.commandDesc.option

enum class OptionType {
    GNU {
        override fun test(option: String) = Regex("--[a-z][a-zA-Z-]*").matches(option)
    },
    UNIX {
        override fun test(option: String): Boolean = Regex("-[a-zA-Z]").matches(option)
    },
    NON_STD {
        override fun test(option: String): Boolean = Regex("-[a-zA-Z-,=]*").matches(option)
    };

    abstract fun test(option: String): Boolean
}