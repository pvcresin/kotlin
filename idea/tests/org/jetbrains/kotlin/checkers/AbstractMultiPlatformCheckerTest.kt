/*
 * Copyright 2010-2016 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.kotlin.checkers

import java.io.File

abstract class AbstractMultiPlatformCheckerTest : AbstractPsiCheckerTest() {

    override fun doTest(filePath: String) {
        val root = File(filePath).apply { assert(exists()) }
        val commonSrc = File(root, "common.kt")
        val jsSrc = File(root, "js.kt")
        val jvmSrc = File(root, "jvm.kt")

        myFixture.configureByFiles(commonSrc.path, jsSrc.path, jvmSrc.path)
        myFixture.checkHighlighting(/*checkWarnings = */false, /*checkInfos = */false, /*checkWeakWarnings = */false)
    }
}