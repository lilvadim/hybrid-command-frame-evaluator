package ru.nsu.hybrid.dsl.ext

import ru.nsu.hybrid.dsl.api.HybridDsl

@HybridDsl
interface DslExtensionMixin<T> {
    val ctx: T
}

