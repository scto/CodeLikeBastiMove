package com.scto.codelikebastimove.feature.treeview

import com.scto.codelikebastimove.core.templates.api.ProjectFile
import java.io.File

object TreeViewUtils {
    
    fun buildTreeFromProjectFiles(files: List<ProjectFile>, projectName: String): List<TreeNodeData> {
        val rootNode = TreeNodeData(
            name = projectName,
            path = projectName,
            isDirectory = true,
            children = mutableListOf()
        )
        
        val nodeMap = mutableMapOf<String, MutableList<TreeNodeData>>()
        nodeMap[""] = mutableListOf()
        
        val sortedFiles = files.sortedWith(compareBy({ !it.isDirectory }, { it.relativePath }))
        
        for (file in sortedFiles) {
            val parts = file.relativePath.split("/")
            val fileName = parts.last()
            val parentPath = parts.dropLast(1).joinToString("/")
            
            val node = TreeNodeData(
                name = fileName,
                path = file.relativePath,
                isDirectory = file.isDirectory,
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
    
    fun buildTreeFromDirectory(directory: File): List<TreeNodeData> {
        if (!directory.exists() || !directory.isDirectory) {
            return emptyList()
        }
        
        return listOf(buildNodeFromFile(directory))
    }
    
    private fun buildNodeFromFile(file: File): TreeNodeData {
        val children = if (file.isDirectory) {
            file.listFiles()
                ?.filter { !it.name.startsWith(".") }
                ?.map { buildNodeFromFile(it) }
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
