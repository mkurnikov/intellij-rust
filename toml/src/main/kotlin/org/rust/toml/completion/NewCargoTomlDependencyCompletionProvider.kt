/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.rust.toml.completion

import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.CompletionUtil
import com.intellij.codeInsight.completion.PrioritizedLookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.icons.AllIcons
import com.intellij.openapi.components.service
import org.rust.toml.StringValueInsertionHandler
import org.toml.lang.psi.TomlKeyValue

class NewCargoTomlDependencyCompletionProvider : TomlKeyValueCompletionProviderBase() {
    override fun completeKey(keyValue: TomlKeyValue, result: CompletionResultSet) {
        val prefix = CompletionUtil.getOriginalElement(keyValue.key)?.text ?: return
        val variants = keyValue.project.service<CargoRegistryIndexService>().crates.values

        result.withPrefixMatcher(CargoNormalizedNamesPrefixMatcher(prefix)).addAllElements(variants.map { variant ->
            PrioritizedLookupElement.withPriority(
                LookupElementBuilder
                    .create(variant.dependencyLine)
                    .withIcon(AllIcons.Nodes.PpLib),
                1.0 / variant.name.length
            )
        })
    }

    override fun completeValue(keyValue: TomlKeyValue, result: CompletionResultSet) {
        val name = CompletionUtil.getOriginalElement(keyValue.key)?.text ?: return
        val variants = keyValue.project.service<CargoRegistryIndexService>().crates[name]?.versions?.map { it.version } ?: return

        result.addAllElements(variants.mapIndexed { index, variant ->
            PrioritizedLookupElement.withPriority(
                LookupElementBuilder
                    .create(variant)
                    .withInsertHandler(StringValueInsertionHandler(keyValue)),
                    index.toDouble()
            )
        })
    }
}

val CargoRegistryCrate.dependencyLine: String
    get() = "$name = \"${maxVersion ?: ""}\""
