// TODO: muted automatically, investigate should it be ran for JS or not
// IGNORE_BACKEND_WITHOUT_CHECK: JS

// WITH_REFLECT

import kotlin.test.*

fun Int.foo(s: String) {}

class A {
    fun bar() {}
}

fun baz(name: String) {}

fun box(): String {
    assertEquals(
            listOf("extension receiver of ${Int::foo}", "parameter #1 s of ${Int::foo}"),
            Int::foo.parameters.map(Any::toString)
    )

    assertEquals(
            listOf("instance of ${A::bar}"),
            A::bar.parameters.map(Any::toString)
    )

    assertEquals(
            listOf("parameter #0 name of ${::baz}"),
            ::baz.parameters.map(Any::toString)
    )

    return "OK"
}
