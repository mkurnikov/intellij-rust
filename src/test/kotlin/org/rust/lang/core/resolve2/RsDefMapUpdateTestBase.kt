/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.rust.lang.core.resolve2

import com.intellij.openapi.application.impl.NonBlockingReadActionImpl
import org.rust.RsTestBase
import org.rust.lang.core.crate.Crate
import org.rust.lang.core.psi.RsFile
import org.rust.openapiext.toPsiFile

abstract class RsDefMapUpdateTestBase : RsTestBase() {

    protected fun doTest(action: () -> Unit, shouldChange: Boolean) {
        val crateRoot = myFixture.findFileInTempDir("main.rs").toPsiFile(myFixture.project) as RsFile
        // Note: crate can change after `action`
        val getTimestamp = { getDefMap(crateRoot.crate!!).timestamp }
        val oldStamp = getTimestamp()
        action()
        val newStamp = getTimestamp()
        val changed = newStamp != oldStamp
        check(changed == shouldChange) { "DefMap should ${if (shouldChange) "" else "not "}rebuilt" }
    }

    private fun getDefMap(crate: Crate): CrateDefMap {
        NonBlockingReadActionImpl.waitForAsyncTaskCompletion()
        return project.defMapService.getOrUpdateIfNeeded(crate.id!!)!!
    }
}
