/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.rust.lang.core.completion.ml

//import com.intellij.internal.ml.DecisionFunction
//import com.intellij.internal.ml.ModelMetadata
//import com.intellij.internal.ml.completion.CompletionRankingModelBase
//import com.intellij.internal.ml.completion.JarCompletionModelProvider
//import com.intellij.lang.Language
//
//@Suppress("UnstableApiUsage")
//class RustMLRankingProvider : JarCompletionModelProvider("Rust", "rust_features") {
//    override fun createModel(metadata: ModelMetadata): DecisionFunction =
//        object : CompletionRankingModelBase(metadata) {
//            override fun predict(features: DoubleArray?): Double = MLGlassBox.makePredict(features)
//        }
//
//    override fun isLanguageSupported(language: Language): Boolean =
//        language.id.compareTo("Rust", ignoreCase = true) == 0
//}
