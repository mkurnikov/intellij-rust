/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.rust.toml.completion

import com.intellij.codeInsight.completion.PrefixMatcher
import kotlin.math.pow

class NewCargoNamesPrefixMatcher(prefix: String): PrefixMatcher(prefix) {
    // does not work
    override fun prefixMatches(name: String): Boolean =
        name.replace('-', '_').startsWith(prefix.replace('-', '_'))

    override fun cloneWithPrefix(prefix: String): PrefixMatcher =
        NewCargoNamesPrefixMatcher(prefix)

}

class CargoNamesPrefixMatcher(prefix: String): PrefixMatcher(prefix) {
    override fun prefixMatches(name: String): Boolean =
        UncanonicalizedNamesIterator(prefix).asSequence().any { name.startsWith(it) }

    override fun cloneWithPrefix(prefix: String): PrefixMatcher =
        CargoNamesPrefixMatcher(prefix)

}

class UncanonicalizedNamesIterator(val input: String) : Iterator<String> {
    val hyphenUnderscoreCount = input.count { it in listOf('_', '-') }
    var hyphenUnderscoreNum = 0

    override fun hasNext(): Boolean = hyphenUnderscoreNum < (2.0).pow(hyphenUnderscoreCount)

    override fun next(): String {
        val newStr = StringBuilder()

        val binaryMapping = Integer.toBinaryString(hyphenUnderscoreNum)
        var binaryIndex = 0

        for (c in input) {
            when (c) {
                '_', '-' -> newStr.append(when (binaryMapping[binaryIndex]) {
                    '0' -> '_'
                    '1' -> '-'
                    else -> '?'
                })
                else -> newStr.append(c)
            }

            binaryIndex += 1
        }

        hyphenUnderscoreNum += 1

        return newStr.toString()
    }
}
