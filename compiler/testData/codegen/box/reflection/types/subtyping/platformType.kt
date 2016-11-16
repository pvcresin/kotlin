// TODO: muted automatically, investigate should it be ran for JS or not
// IGNORE_BACKEND_WITHOUT_CHECK: JS

// WITH_REFLECT
// FILE: J.java

public interface J {
    String platformString();
}

// FILE: K.kt

import kotlin.reflect.*
import kotlin.test.assertTrue

fun string(): String = null!!
fun nullableString(): String? = null!!

fun check(subCallable: KCallable<*>, superCallable: KCallable<*>) {
    val subtype = subCallable.returnType
    val supertype = superCallable.returnType
    assertTrue(subtype.isSubtypeOf(supertype))
    assertTrue(supertype.isSupertypeOf(subtype))
}

fun box(): String {
    check(::string, J::platformString)
    check(J::platformString, ::string)
    check(::nullableString, J::platformString)
    check(J::platformString, ::nullableString)
    check(J::platformString, J::platformString)

    return "OK"
}
