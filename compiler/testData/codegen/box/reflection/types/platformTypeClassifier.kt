// TODO: muted automatically, investigate should it be ran for JS or not
// IGNORE_BACKEND_WITHOUT_CHECK: JS

// WITH_REFLECT
// FILE: J.java

import java.util.List;

public class J {
    public static String string() {
        return "";
    }

    public static List<Object> list() {
        return null;
    }

    public static int primitiveInt() {
        return 0;
    }

    public static Integer wrapperInt() {
        return 0;
    }
}

// FILE: K.kt

import kotlin.reflect.KClass
import kotlin.test.assertEquals

fun box(): String {
    assertEquals(String::class, J::string.returnType.classifier)
    assertEquals(List::class, J::list.returnType.classifier)

    assertEquals(Int::class.javaPrimitiveType!!, (J::primitiveInt.returnType.classifier as KClass<*>).java)
    assertEquals(Int::class.javaObjectType, (J::wrapperInt.returnType.classifier as KClass<*>).java)

    return "OK"
}
