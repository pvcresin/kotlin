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

package kotlin.text

public typealias Appendable = java.lang.Appendable
public typealias StringBuilder = java.lang.StringBuilder

// NOTE: If you're adding type aliases to types from java.lang, do not forget to exclude aliased types from
// imported by default java.lang package. See JvmPlatform.defaultModuleParameters.excludedImports property.