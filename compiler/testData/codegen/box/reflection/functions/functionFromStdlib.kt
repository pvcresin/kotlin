// TODO: muted automatically, investigate should it be ran for JS or not
// IGNORE_BACKEND_WITHOUT_CHECK: JS

// WITH_REFLECT

fun doStuff(fn: String.() -> String) = "ok".fn()

fun box(): String {
    return doStuff(String::toUpperCase)
}
