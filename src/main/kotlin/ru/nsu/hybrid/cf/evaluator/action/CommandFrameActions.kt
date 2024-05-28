package ru.nsu.hybrid.cf.evaluator.action

open class CommandFrameActions(
    val scriptContext: String,
    val actionMap: Map<ActionDescriptor, String>
)

data class ActionDescriptor(
    val elementId: String,
)

