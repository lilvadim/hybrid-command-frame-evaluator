package ru.nsu.hybrid.cf.evaluator

import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.jupiter.api.Test
import ru.nsu.hybrid.cf.evaluator.action.JsBuilder

class JsBuilderTest {

    val uut = JsBuilder()

    @Test
    fun call() {
        val paramsObj = object {
            val prop1 = "Prop1Value"
            val prop2 = object {
                val nestedProp = "NestedPropValue"
            }
        }

        val expected = """window.testApi.test({ "prop1": "Prop1Value", "prop2": { "nestedProp": "NestedPropValue" } });"""

        val actual = uut.call("window", "testApi", "test") { paramsObj }

        assertThat(actual)
            .isEqualToIgnoringWhitespace(expected)
    }

    @Test
    fun function() {
        val paramsObj = object {
            val prop1 = "Prop1Value"
            val prop2 = object {
                val nestedProp = "NestedPropValue"
            }
        }

        val expected = """
            function testFunction() {
                window.testApi.method1({ "prop1": "Prop1Value", "prop2": { "nestedProp": "NestedPropValue" } });
                window.testApi.method2({ "prop1": "Prop1Value", "prop2": { "nestedProp": "NestedPropValue" } });
            }
        """.trimIndent()

        val actual = uut.function("testFunction") {
            call("window.testApi", "method1") { paramsObj }
            call("window.testApi", "method2") { paramsObj }
        }.also { println(it) }

        assertThat(actual).isEqualToIgnoringWhitespace(expected)
    }

    @Test
    fun call_withoutParams() {
        val expected = """window.testApi.test();"""

        val actual = uut.call("window", "testApi", "test")

        assertThat(actual)
            .isEqualToIgnoringWhitespace(expected)
    }
}