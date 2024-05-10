package ru.nsu.hybrid.dsl.ext

import ru.nsu.hybrid.dsl.builder.HybridDsl

@HybridDsl
interface DslExtensionMixin<T> {
    val ctx: T
}

