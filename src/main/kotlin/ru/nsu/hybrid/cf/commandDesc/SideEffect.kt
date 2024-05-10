package ru.nsu.hybrid.cf.commandDesc

import ru.nsu.hybrid.cf.commandDesc.option.OptionRef

class SideEffect(
    val effectType: Type,
    val optionRef: OptionRef
) {
    enum class Type {
        INCLUDE,
        EXCLUDE
    }

    companion object Factory {
        fun include(optionRef: String) = SideEffect(
            effectType = Type.INCLUDE,
            optionRef = OptionRef(optionRef)
        )

        fun exclude(optionRef: String) = SideEffect(
            effectType = Type.EXCLUDE,
            optionRef = OptionRef(optionRef)
        )
    }
}

