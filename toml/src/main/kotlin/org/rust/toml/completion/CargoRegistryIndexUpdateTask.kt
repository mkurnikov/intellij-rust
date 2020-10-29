/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.rust.toml.completion

import com.intellij.openapi.components.service
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project

class CargoRegistryIndexUpdateTask(project: Project) : Task.Backgroundable(project, "Updating cargo registry index", false) {
    override fun run(indicator: ProgressIndicator) {
        project.service<CargoRegistryIndexService>().reloadCrates()
    }
}
