// TODO: muted automatically, investigate should it be ran for JS or not
// IGNORE_BACKEND_WITHOUT_CHECK: JS

// WITH_REFLECT

@JvmName("Fail")
fun OK() {}

fun box() = ::OK.name
