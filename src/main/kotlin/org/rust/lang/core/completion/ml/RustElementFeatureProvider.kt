/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.rust.lang.core.completion.ml

import com.intellij.codeInsight.completion.CompletionLocation
import com.intellij.codeInsight.completion.ml.ContextFeatures
import com.intellij.codeInsight.completion.ml.ElementFeatureProvider
import com.intellij.codeInsight.completion.ml.MLFeatureValue
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.psi.PsiElement
import org.rust.lang.core.psi.RsFunction
import org.rust.lang.core.psi.RsStructItem

@Suppress("UnstableApiUsage")
class RustElementFeatureProvider : ElementFeatureProvider {
    override fun getName(): String = "rust"

    override fun calculateFeatures(
        element: LookupElement,
        location: CompletionLocation,
        contextFeatures: ContextFeatures
    ): MutableMap<String, MLFeatureValue> {
        val result = hashMapOf<String, MLFeatureValue>()

        val kind = RustMLCompletionElementKind.fromLookupElement(element)
        if (kind != null) {
            result["kind"] = MLFeatureValue.categorical(kind)
        }

        return result
    }
}

private enum class RustMLCompletionElementKind(val psiClasses: Set<Class<out PsiElement>>) {

    Struct(setOf(RsStructItem::class.java)),
    Function(setOf(RsFunction::class.java));
    //TODO

    companion object {
        fun fromLookupElement(element: LookupElement): RustMLCompletionElementKind? {
            val psiElement = element.psiElement ?: return null

            for (kind in values()) {
                if (psiElement.javaClass in kind.psiClasses) {
                    return kind
                }
            }

            return null
        }
    }
}
