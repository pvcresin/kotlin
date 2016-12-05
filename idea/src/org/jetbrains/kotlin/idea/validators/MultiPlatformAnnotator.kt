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

package org.jetbrains.kotlin.idea.validators

import com.intellij.codeInsight.daemon.Validator
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.ExternalAnnotator
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtFile

class MultiPlatformAnnotator : ExternalAnnotator<MultiPlatformAnnotator.MyHost, MultiPlatformAnnotator.MyHost>() {

    override fun collectInformation(file: PsiFile): MyHost? {
        if (file !is KtFile) return null

        val validator = PlatformHeaderValidator()
        val host = MyHost()
        validator.validate(file, host)

        return host
    }

    override fun doAnnotate(collectedInfo: MyHost?) = collectedInfo

    override fun apply(file: PsiFile, annotationResult: MyHost?, holder: AnnotationHolder) {
        annotationResult?.apply(holder)
    }

    inner class MyHost : Validator.ValidationHost {

        private val messages = mutableListOf<Triple<KtDeclaration, String, Validator.ValidationHost.ErrorType>>()

        override fun addMessage(context: PsiElement?, message: String?, type: Int) =
                throw UnsupportedOperationException()

        override fun addMessage(context: PsiElement?, message: String?, type: Validator.ValidationHost.ErrorType) {
            if (context is KtDeclaration && message != null) {
                messages += Triple(context, message, type)
            }
        }

        fun apply(holder: AnnotationHolder) {
            for ((declaration, message, type) in messages) {
                addMessage(declaration, message, type, holder)
            }
        }

    }

    private fun addMessage(
            declaration: KtDeclaration,
            message: String,
            type: Validator.ValidationHost.ErrorType,
            holder: AnnotationHolder
    ) {
        when (type) {
            Validator.ValidationHost.ErrorType.ERROR -> holder.createErrorAnnotation(declaration, message)
            Validator.ValidationHost.ErrorType.WARNING -> holder.createWarningAnnotation(declaration, message)
            Validator.ValidationHost.ErrorType.INFO -> holder.createInfoAnnotation(declaration, message)
        }

    }
}