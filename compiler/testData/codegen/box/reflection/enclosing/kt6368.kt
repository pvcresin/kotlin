// TODO: muted automatically, investigate should it be ran for JS or not
// IGNORE_BACKEND_WITHOUT_CHECK: JS

// WITH_REFLECT

import java.util.HashMap

interface R {
    fun result(): String
}

val a by lazy {
    with(HashMap<String, R>()) {
        put("result", object : R {
            override fun result(): String = "OK"
        })
        this
    }
}

fun box(): String {
    val r = a["result"]!!

    // Check that reflection won't fail
    r.javaClass.getEnclosingMethod().toString()

    return r.result()
}
