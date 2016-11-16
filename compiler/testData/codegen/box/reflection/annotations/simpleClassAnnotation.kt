// TODO: muted automatically, investigate should it be ran for JS or not
// IGNORE_BACKEND_WITHOUT_CHECK: JS

// WITH_REFLECT

@Retention(AnnotationRetention.RUNTIME)
annotation class Simple(val value: String)

@Simple("OK")
class A

fun box(): String {
    return (A::class.annotations.single() as Simple).value
}
