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
import com.intellij.codeInsight.daemon.Validator.ValidationHost.ErrorType
import org.jetbrains.kotlin.descriptors.MemberDescriptor
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.diagnostics.Diagnostic
import org.jetbrains.kotlin.diagnostics.DiagnosticSink
import org.jetbrains.kotlin.diagnostics.Severity
import org.jetbrains.kotlin.diagnostics.rendering.DefaultErrorMessages
import org.jetbrains.kotlin.idea.caches.resolve.analyze
import org.jetbrains.kotlin.idea.caches.resolve.findModuleDescriptor
import org.jetbrains.kotlin.idea.project.TargetPlatformDetector
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtVisitorVoid
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.TargetPlatform
import org.jetbrains.kotlin.resolve.checkers.PlatformImplDeclarationChecker

class PlatformHeaderValidator : Validator<KtFile> {
    override fun validate(context: KtFile, host: Validator.ValidationHost) {
        val platform = TargetPlatformDetector.getPlatform(context)
        if (platform != TargetPlatform.Default) return

        val module = context.findModuleDescriptor()
        val dependents = module.allDependentModules // TODO: filter dependents with Default platform?

        val validationVisitor = ValidationVisitor(context.analyze(), dependents, host)
        context.accept(validationVisitor)
    }

    class ValidationVisitor(
            val bindingContext: BindingContext,
            modulesToCheck: List<ModuleDescriptor>,
            host: Validator.ValidationHost
    ) : KtVisitorVoid() {

        private fun Severity.toErrorType(): ErrorType = when (this) {
            Severity.ERROR -> ErrorType.ERROR
            Severity.WARNING -> ErrorType.WARNING
            Severity.INFO -> ErrorType.INFO
        }

        private val diagnosticSink = object : DiagnosticSink {
            override fun report(diagnostic: Diagnostic) {
                host.addMessage(diagnostic.psiElement, DefaultErrorMessages.render(diagnostic), diagnostic.severity.toErrorType())
            }

            override fun wantsDiagnostics() = true
        }

        private val checkers = modulesToCheck.map(::PlatformImplDeclarationChecker)

        private fun validateDeclaration(declaration: KtDeclaration) {
            val descriptor = bindingContext[BindingContext.DECLARATION_TO_DESCRIPTOR, declaration] as? MemberDescriptor ?: return
            for (checker in checkers) {
                checker.checkPlatformDeclarationHasDefinition(declaration, descriptor, diagnosticSink, checkImpl = true)
            }
        }

        override fun visitKtFile(file: KtFile) {
            for (declaration in file.declarations) {
                declaration.accept(this)
            }
        }

        override fun visitDeclaration(dcl: KtDeclaration) {
            validateDeclaration(dcl)
        }
    }
}