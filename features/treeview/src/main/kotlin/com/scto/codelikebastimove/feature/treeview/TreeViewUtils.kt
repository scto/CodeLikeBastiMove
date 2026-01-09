package com.scto.codelikebastimove.feature.treeview

import com.scto.codelikebastimove.core.templates.api.ProjectFile
import java.io.File

object TreeViewUtils {

    fun buildTreeFromProjectFiles(
        files: List<ProjectFile>,
        projectName: String,
        includeHidden: Boolean = true
    ): List<TreeNodeData> {
        val rootNode = TreeNodeData(
            name = projectName,
            path = projectName,
            isDirectory = true,
            children = mutableListOf()
        )

        val allPaths = mutableSetOf<String>()
        val fileMap = mutableMapOf<String, ProjectFile>()

        for (file in files) {
            if (!includeHidden && file.relativePath.split("/").any { it.startsWith(".") }) {
                continue
            }
            fileMap[file.relativePath] = file
            allPaths.add(file.relativePath)

            var path = file.relativePath
            while (path.contains("/")) {
                path = path.substringBeforeLast("/")
                if (path.isNotEmpty()) {
                    allPaths.add(path)
                }
            }
        }

        val nodeMap = mutableMapOf<String, MutableList<TreeNodeData>>()
        nodeMap[""] = mutableListOf()

        val sortedPaths = allPaths.sortedWith(compareBy({
            val file = fileMap[it]
            file == null || !file.isDirectory
        }, { it }))

        for (path in sortedPaths) {
            val parts = path.split("/")
            val fileName = parts.last()
            val parentPath = parts.dropLast(1).joinToString("/")

            val existingFile = fileMap[path]
            val isDirectory = existingFile?.isDirectory ?: true

            val node = TreeNodeData(
                name = fileName,
                path = path,
                isDirectory = isDirectory,
                children = emptyList()
            )

            nodeMap.getOrPut(parentPath) { mutableListOf() }.add(node)
        }

        fun buildTree(path: String): List<TreeNodeData> {
            val children = nodeMap[path] ?: return emptyList()
            return children.map { node ->
                if (node.isDirectory) {
                    node.copy(children = buildTree(node.path))
                } else {
                    node
                }
            }.sortedWith(compareBy({ !it.isDirectory }, { it.name.lowercase() }))
        }

        return listOf(
            rootNode.copy(children = buildTree(""))
        )
    }

    fun buildTreeFromDirectory(
        directory: File,
        includeHidden: Boolean = true
    ): List<TreeNodeData> {
        if (!directory.exists() || !directory.isDirectory) {
            return emptyList()
        }

        return listOf(buildNodeFromFile(directory, includeHidden))
    }

    private fun buildNodeFromFile(file: File, includeHidden: Boolean): TreeNodeData {
        val children = if (file.isDirectory) {
            file.listFiles()
                ?.filter { includeHidden || !it.name.startsWith(".") }
                ?.map { buildNodeFromFile(it, includeHidden) }
                ?.sortedWith(compareBy({ !it.isDirectory }, { it.name.lowercase() }))
                ?: emptyList()
        } else {
            emptyList()
        }

        return TreeNodeData(
            name = file.name,
            path = file.absolutePath,
            isDirectory = file.isDirectory,
            children = children
        )
    }
}
