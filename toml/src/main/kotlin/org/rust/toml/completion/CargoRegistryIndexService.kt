/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.rust.toml.completion

import com.google.gson.Gson
import com.intellij.openapi.project.Project
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.eclipse.jgit.treewalk.TreeWalk
import org.rust.taskQueue
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.file.Paths

class CargoRegistryIndexService(project: Project) {
//    init {
//        project.messageBus.connect().subscribe(CargoProjectsService.CARGO_PROJECTS_TOPIC, object : CargoProjectsService.CargoProjectsListener {
//            override fun cargoProjectsUpdated(service: CargoProjectsService, projects: Collection<CargoProject>) {
//                cargoProjectsModTracker.incModificationCount()
//            }
//        })
//    }

    private val homeDir: String = System.getProperty("user.home")

    private val repository: Repository = FileRepositoryBuilder()
        .setGitDir(Paths.get(homeDir, ".cargo/registry/index/github.com-1ecc6299db9ec823/.git/").toFile())
        .build()

    var crates = hashMapOf<String, CargoRegistryCrate>()

    init {
        project.taskQueue.run(CargoRegistryIndexUpdateTask(project))
    }

    fun reloadCrates() {
        val branch: ObjectId = repository.resolve("origin/master")
        val revCommit = RevWalk(repository).parseCommit(ObjectId.fromString(branch.name))
        val tree = revCommit.tree
        val gson = Gson()

        TreeWalk(repository).use { treeWalk ->
            treeWalk.addTree(tree)
            treeWalk.isRecursive = true
            treeWalk.isPostOrderTraversal = false

            while (treeWalk.next()) {
                val objectId = treeWalk.getObjectId(0)
                val loader = repository.open(objectId)

                val versions = mutableListOf<CargoRegistryCrateVersion>()
                val reader = BufferedReader(InputStreamReader(loader.openStream()))
                reader.forEachLine { line ->
                    if (line.isBlank()) return@forEachLine

                    try {
                        val parsedVersion = gson.fromJson(line, ParsedVersion::class.java)
                        versions.add(CargoRegistryCrateVersion(parsedVersion.vers, parsedVersion.features.map { it.key }))
                    } catch (e: Exception) {
                        println("${treeWalk.pathString}: ${e.message}")
                    }
                }
                crates[treeWalk.nameString] = CargoRegistryCrate(treeWalk.nameString, versions)
            }
        }
    }
}

data class CargoRegistryCrate(val name: String, val versions: List<CargoRegistryCrateVersion>)
data class CargoRegistryCrateVersion(val version: String, val features: List<String>)

data class ParsedVersion(val name: String, val vers: String, val features: HashMap<String, List<String>>)

val CargoRegistryCrate.maxVersion: String?
    get() = versions.lastOrNull()?.version
