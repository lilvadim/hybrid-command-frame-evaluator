package ru.nsu.hybrid.cf.evaluator.action

import com.fasterxml.jackson.databind.ObjectMapper

open class JsBuilder {

    private val objectMapper = ObjectMapper()

    open fun call(vararg path: String, params: (() -> Any)? = null): String {
        val paramsJson = params?.invoke()?.let { objectMapper.writeValueAsString(it) } ?: ""
        return path.joinToString(separator = ".") + "($paramsJson);"
    }

    fun function(name: String, paramsDefRaw: String = "",functionBody: FunctionBodyBuilder.() -> Unit): String {
        return "function $name($paramsDefRaw) {\n" +
                "${FunctionBodyBuilder().apply(functionBody).body}" +
                "}\n"
    }

    class FunctionBodyBuilder : JsBuilder() {
        val body = StringBuilder()
        override fun call(vararg path: String, params: (() -> Any)?): String {
            body.append(super.call(path = path, params = params) + "\n")
            return body.toString()
        }
    }
}