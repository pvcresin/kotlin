// TODO: muted automatically, investigate should it be ran for JS or not
// IGNORE_BACKEND_WITHOUT_CHECK: JS

// WITH_REFLECT

class A(val result: String)

fun box(): String {
    val a = (::A).call("OK")
    return a.result
}
