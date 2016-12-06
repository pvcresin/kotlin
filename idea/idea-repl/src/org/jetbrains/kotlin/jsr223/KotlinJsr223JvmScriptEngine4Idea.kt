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

package org.jetbrains.kotlin.jsr223

import com.intellij.openapi.Disposable
import org.jetbrains.kotlin.cli.common.repl.GenericReplCompiledEvaluator
import org.jetbrains.kotlin.cli.common.repl.KotlinJsr223JvmScriptEngineBase
import org.jetbrains.kotlin.cli.common.repl.ReplCompiledEvaluator
import org.jetbrains.kotlin.cli.common.repl.ReplCompiler
import org.jetbrains.kotlin.daemon.client.DaemonReportMessage
import org.jetbrains.kotlin.daemon.client.DaemonReportingTargets
import org.jetbrains.kotlin.daemon.client.KotlinCompilerClient
import org.jetbrains.kotlin.daemon.client.KotlinRemoteReplCompiler
import org.jetbrains.kotlin.daemon.common.*
import org.jetbrains.kotlin.utils.PathUtil
import java.io.File
import javax.script.ScriptContext
import javax.script.ScriptEngineFactory
import javax.script.ScriptException

class KotlinJsr223JvmScriptEngine4Idea(
        disposable: Disposable,
        factory: ScriptEngineFactory,
        templateClasspath: List<File>,
        templateClassName: String,
        getScriptArgs: (ScriptContext) -> Array<Any?>?,
        scriptArgsTypes: Array<Class<*>>?
) : KotlinJsr223JvmScriptEngineBase(factory) {

    private val daemon by lazy {
        val path = PathUtil.getKotlinPathsForIdeaPlugin().compilerPath
        assert(path.exists())
        val compilerId = CompilerId.makeCompilerId(path)
        val daemonOptions = configureDaemonOptions()
        val daemonJVMOptions = DaemonJVMOptions()

        val daemonReportMessages = arrayListOf<DaemonReportMessage>()

        KotlinCompilerClient.connectToCompileService(compilerId, daemonJVMOptions, daemonOptions, DaemonReportingTargets(null, daemonReportMessages), true, true)
                ?: throw ScriptException("Unable to connect to repl server:" + daemonReportMessages.joinToString("\n  ", prefix = "\n  ") { "${it.category.name} ${it.message}" })
    }

    override val replCompiler: ReplCompiler by lazy {
        daemon.let {
            KotlinRemoteReplCompiler(disposable,
                                     it,
                                     makeAutodeletingFlagFile("idea-jsr223-repl-session"),
                                     CompileService.TargetPlatform.JVM,
                                     templateClasspath,
                                     templateClassName,
                                     System.out)
        }
    }

    // TODO: bindings passing works only once on the first eval, subsequent setContext/setBindings call have no effect. Consider making it dynamic, but take history into account
    val localEvaluator: ReplCompiledEvaluator by lazy { GenericReplCompiledEvaluator(templateClasspath, Thread.currentThread().contextClassLoader, getScriptArgs(getContext()), scriptArgsTypes) }

    override val replEvaluator: ReplCompiledEvaluator get() = localEvaluator
}
