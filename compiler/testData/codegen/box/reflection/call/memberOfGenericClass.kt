// TODO: muted automatically, investigate should it be ran for JS or not
// IGNORE_BACKEND_WITHOUT_CHECK: JS

// WITH_REFLECT

var result = "Fail"

class A<T> {
    fun foo(t: T) {
        result = t as String
    }
}

fun box(): String {
    (A<String>::foo).call(A<String>(), "OK")
    return result
}
