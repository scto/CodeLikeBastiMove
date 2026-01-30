package com.scto.codelikebastimove.core.common.utils

import java.util.NoSuchElementException

/**
 * Ein Builder, um einzigartige, gekürzte Namen für Pfade zu generieren (z.B. für Tabs oder Listen),
 * wenn Dateinamen doppelt vorkommen.
 *
 * Basiert auf IntelliJ Community Code:
 *
 * @param <T> Der Typ des Schlüssels (meistens der Pfad oder ein File-Objekt).
 * @see <a
 *   href="https://github.com/JetBrains/intellij-community/blob/master/platform/util/base/src/com/intellij/filename/UniqueNameBuilder.java">UniqueNameBuilder.java</a>
 */
class UniqueNameBuilder<T>(private val root: String, private val separator: String) {

  private val paths: MutableMap<T, String> = HashMap()
  private val rootNode = Node("", null)

  fun contains(file: T): Boolean {
    return paths.containsKey(file)
  }

  fun size(): Int {
    return paths.size
  }

  /**
   * Fügt einen Pfad hinzu und baut den internen Trie (Präfix-Baum) von hinten auf.
   *
   * Beispiel Struktur: /idea/pycharm/download/index.html /idea/fabrique/download/index.html
   *
   * [RootNode] <- [/index.html] <- [/download] <- [/pycharm] <- [/idea] <- [/fabrique] <- [/idea]
   */
  fun addPath(key: T, path: String) {
    val adjustedPath = path.removePrefix(root)
    paths[key] = adjustedPath

    var current = rootNode
    val iterator = PathComponentsIterator(adjustedPath)

    while (iterator.hasNext()) {
      val word = iterator.next()
      current = current.findOrAddChild(word)
    }

    // Zähler aktualisieren (Back-Propagation)
    var c: Node? = current
    while (c != null) {
      c.nestedChildrenCount++
      c = c.parentNode
    }
  }

  fun getShortPath(key: T): String {
    val path = paths[key] ?: return key.toString()

    var current = rootNode
    var firstNodeWithBranches: Node? = null
    var firstNodeBeforeNodeWithBranches: Node? = null
    var fileNameNode: Node? = null

    val iterator = PathComponentsIterator(path)

    while (iterator.hasNext()) {
      val pathComponent = iterator.next()
      current = current.findOrAddChild(pathComponent)

      if (fileNameNode == null) {
        fileNameNode = current
      }

      if (
        firstNodeBeforeNodeWithBranches == null &&
          firstNodeWithBranches != null &&
          current.children.size <= 1
      ) {
        val parent = current.parentNode
        if (parent != null && (parent.nestedChildrenCount - parent.children.size < 1)) {
          firstNodeBeforeNodeWithBranches = current
        }
      }

      if (current.children.size != 1 && firstNodeWithBranches == null) {
        firstNodeWithBranches = current
      }
    }

    if (firstNodeBeforeNodeWithBranches == null) {
      firstNodeBeforeNodeWithBranches = current
    }

    return buildString {
      var c = firstNodeBeforeNodeWithBranches
      var skipFirstSeparator = true

      while (c != null && c !== rootNode) {
        val parent = c!!.parentNode ?: break

        if (
          c !== fileNameNode && c !== firstNodeBeforeNodeWithBranches && parent.children.size == 1
        ) {
          append(separator)
          append("\u2026") // Ellipsis (...)

          // Überspringe Knoten, solange keine Verzweigung da ist
          while (c!!.parentNode !== fileNameNode && c!!.parentNode?.children?.size == 1) {
            c = c!!.parentNode
          }
        } else {
          if (c!!.text.startsWith(VFS_SEPARATOR)) {
            if (!skipFirstSeparator) {
              append(separator)
            }
            skipFirstSeparator = false
            append(c!!.text, VFS_SEPARATOR.length, c!!.text.length)
          } else {
            append(c!!.text)
          }
        }
        c = c!!.parentNode
      }
    }
  }

  fun getSeparator(): String {
    return separator
  }

  // -------------------------------------------------------------------------
  // Helper Classes
  // -------------------------------------------------------------------------

  private class Node(val text: String, val parentNode: Node?) {
    val children = HashMap<String, Node>()
    var nestedChildrenCount: Int = 0

    fun findOrAddChild(word: String): Node {
      return children.getOrPut(word) { Node(word, this) }
    }
  }

  /** Iteriert den Pfad rückwärts basierend auf dem Separator. */
  private class PathComponentsIterator(private val path: String) : Iterator<String> {
    private var lastPos: Int = path.length
    private var separatorPos: Int = path.lastIndexOf(VFS_SEPARATOR)

    override fun hasNext(): Boolean {
      return lastPos != 0
    }

    override fun next(): String {
      if (lastPos == 0) {
        throw NoSuchElementException()
      }
      val pathComponent: String

      if (separatorPos != -1) {
        pathComponent = path.substring(separatorPos, lastPos)
        lastPos = separatorPos
        separatorPos = path.lastIndexOf(VFS_SEPARATOR, lastPos - 1)
      } else {
        pathComponent = path.substring(0, lastPos)
        if (!pathComponent.startsWith(VFS_SEPARATOR)) {
          pathComponent = VFS_SEPARATOR + pathComponent
        }
        lastPos = 0
      }
      return pathComponent
    }
  }

  companion object {
    private const val VFS_SEPARATOR = "/"
  }
}
