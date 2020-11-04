/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.rust.lang.core.resolve2

import org.intellij.lang.annotations.Language
import org.rust.UseNewResolve
import org.rust.lang.core.psi.ext.RsNamedElement
import org.rust.lang.core.psi.ext.RsReferenceElement
import org.rust.lang.core.resolve.RsResolveTestBase

/** See [DefMapService.defMaps] for details */
@UseNewResolve
class RsDefMapSoftReferenceTest : RsResolveTestBase() {
    fun doTest(@Language("Rust") code: String) {
        InlineFile(code)
        val element = findElementInEditor<RsReferenceElement>()
        val reference = element.reference ?: error("Failed to get reference for `${element.text}`")
        val target = findElementInEditor<RsNamedElement>("X")

        check(reference.resolve() == target)

        /** [DefMapHolder] is stored under soft reference */
        forceClearAllSoftReferences()
        check(reference.resolve() == target)
    }

    fun test() = doTest("""
        fn foo() {}
         //X
        fn main() {
            foo();
        } //^
    """)
}

private fun forceClearAllSoftReferences() {
    val blocks = mutableListOf<ByteArray>()
    val blockSize = 100_000_000
    while (true) {
        try {
            blocks += ByteArray(blockSize)
        } catch (e: OutOfMemoryError) {
            return  // success, all soft references are cleared now
        }
    }
}
