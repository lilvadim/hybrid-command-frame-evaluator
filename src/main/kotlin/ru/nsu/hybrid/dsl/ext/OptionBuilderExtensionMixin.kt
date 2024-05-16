package ru.nsu.hybrid.dsl.ext

import ru.nsu.hybrid.dsl.builder.OptionContext

interface OptionBuilderExtensionMixin : DslExtensionMixin<OptionContext> {
    val exclusive
        get() = GroupType.EXCLUSIVE
    val inclusive
        get() = GroupType.INCLUSIVE
    val toggle
        get() = Toggle

    operator fun String.unaryMinus() = ctx.exclude(this)

    operator fun String.unaryPlus() = ctx.exclude(this)

    fun groups(vararg groupIds: String): Groups {
        return Groups(groupIds.toSet())
    }

    operator fun Groups.contains(groupType: GroupType): Boolean {
        when (groupType) {
            GroupType.INCLUSIVE -> ctx.inclusiveInGroups = this.values
            GroupType.EXCLUSIVE -> ctx.exclusiveInGroups = this.values
        }
        return true
    }

    operator fun String.contains(groupType: GroupType): Boolean {
        groups(this).contains(groupType)
        return true
    }

    fun options(vararg optionRefs: String): OptionRefs {
        return OptionRefs(optionRefs.toSet())
    }

    infix fun Toggle.on(optionRef: String) {
        ctx.exclude(optionRef)
    }

    infix fun Toggle.off(optionRef: String) {
        ctx.include(optionRef)
    }

    infix fun Toggle.on(optionRefs: OptionRefs) {
        optionRefs.values.forEach { ctx.exclude(it) }
    }

    infix fun Toggle.off(optionRefs: OptionRefs) {
        optionRefs.values.forEach { ctx.include(it) }
    }

    object Toggle

    class Groups(val values: Set<String>)

    class OptionRefs(val values: Set<String>)

    enum class GroupType {
        INCLUSIVE,
        EXCLUSIVE,
    }
}