package ru.nsu.hybrid.cf.commandDesc.entry

fun traverse(root: Entry, callback: (Entry) -> Unit) {
    callback(root)
    root.entries?.forEach { traverse(it, callback) }
}

