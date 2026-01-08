package com.scto.codelikebastimove.feature.assetstudio.converter

import androidx.compose.ui.graphics.Color
import com.scto.codelikebastimove.feature.assetstudio.model.AVDDocument
import com.scto.codelikebastimove.feature.assetstudio.model.VectorGroup
import com.scto.codelikebastimove.feature.assetstudio.model.VectorPath
import java.util.UUID

class SvgToAvdConverter {

    fun convertSvgToAvd(svgContent: String, name: String = "converted_icon"): Result<AVDDocument> {
        return try {
            val viewBox = extractViewBox(svgContent)
            val width = extractAttribute(svgContent, "width")?.removeSuffix("px")?.toFloatOrNull() ?: viewBox.third
            val height = extractAttribute(svgContent, "height")?.removeSuffix("px")?.toFloatOrNull() ?: viewBox.fourth
            
            val paths = extractPaths(svgContent)
            val groups = extractGroups(svgContent)
            
            val rootGroup = VectorGroup(
                id = "root",
                name = name,
                paths = paths,
                groups = groups
            )
            
            Result.success(AVDDocument(
                name = name,
                width = width,
                height = height,
                viewportWidth = viewBox.third,
                viewportHeight = viewBox.fourth,
                rootGroup = rootGroup
            ))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun convertAvdToXml(document: AVDDocument): String {
        return buildString {
            appendLine("""<?xml version="1.0" encoding="utf-8"?>""")
            appendLine("""<vector xmlns:android="http://schemas.android.com/apk/res/android"""")
            appendLine("""    android:width="${document.width}dp"""")
            appendLine("""    android:height="${document.height}dp"""")
            appendLine("""    android:viewportWidth="${document.viewportWidth}"""")
            appendLine("""    android:viewportHeight="${document.viewportHeight}"""")
            if (document.autoMirrored) {
                appendLine("""    android:autoMirrored="true"""")
            }
            document.tint?.let { tint ->
                appendLine("""    android:tint="${colorToHex(tint)}"""")
            }
            appendLine(""">""")
            
            appendGroup(this, document.rootGroup, 1)
            
            appendLine("""</vector>""")
        }
    }

    fun convertAvdToSvg(document: AVDDocument): String {
        return buildString {
            appendLine("""<?xml version="1.0" encoding="UTF-8"?>""")
            appendLine("""<svg xmlns="http://www.w3.org/2000/svg" """)
            appendLine("""     width="${document.width}" height="${document.height}" """)
            appendLine("""     viewBox="0 0 ${document.viewportWidth} ${document.viewportHeight}">""")
            
            appendSvgGroup(this, document.rootGroup, 1)
            
            appendLine("""</svg>""")
        }
    }

    fun convertAvdToComposeImageVector(document: AVDDocument, packageName: String = "com.example"): String {
        val iconName = document.name.split("_").joinToString("") { it.replaceFirstChar { c -> c.uppercase() } }
        
        return buildString {
            appendLine("package $packageName")
            appendLine()
            appendLine("import androidx.compose.ui.graphics.Color")
            appendLine("import androidx.compose.ui.graphics.PathFillType")
            appendLine("import androidx.compose.ui.graphics.SolidColor")
            appendLine("import androidx.compose.ui.graphics.StrokeCap")
            appendLine("import androidx.compose.ui.graphics.StrokeJoin")
            appendLine("import androidx.compose.ui.graphics.vector.ImageVector")
            appendLine("import androidx.compose.ui.graphics.vector.path")
            appendLine("import androidx.compose.ui.unit.dp")
            appendLine()
            appendLine("val $iconName: ImageVector")
            appendLine("    get() {")
            appendLine("        if (_$iconName != null) {")
            appendLine("            return _$iconName!!")
            appendLine("        }")
            appendLine("        _$iconName = ImageVector.Builder(")
            appendLine("            name = \"$iconName\",")
            appendLine("            defaultWidth = ${document.width}.dp,")
            appendLine("            defaultHeight = ${document.height}.dp,")
            appendLine("            viewportWidth = ${document.viewportWidth}f,")
            appendLine("            viewportHeight = ${document.viewportHeight}f")
            appendLine("        ).apply {")
            
            appendComposePathsForGroup(this, document.rootGroup, 3)
            
            appendLine("        }.build()")
            appendLine("        return _$iconName!!")
            appendLine("    }")
            appendLine()
            appendLine("private var _$iconName: ImageVector? = null")
        }
    }

    private fun appendGroup(builder: StringBuilder, group: VectorGroup, indent: Int) {
        val indentStr = "    ".repeat(indent)
        
        for (path in group.paths) {
            builder.appendLine("$indentStr<path")
            builder.appendLine("$indentStr    android:pathData=\"${path.pathData}\"")
            builder.appendLine("$indentStr    android:fillColor=\"${colorToHex(path.fillColor)}\"")
            if (path.strokeColor != null) {
                builder.appendLine("$indentStr    android:strokeColor=\"${colorToHex(path.strokeColor)}\"")
                builder.appendLine("$indentStr    android:strokeWidth=\"${path.strokeWidth}\"")
            }
            if (path.fillAlpha < 1f) {
                builder.appendLine("$indentStr    android:fillAlpha=\"${path.fillAlpha}\"")
            }
            builder.appendLine("$indentStr/>")
        }
        
        for (childGroup in group.groups) {
            val hasTransforms = childGroup.rotation != 0f || childGroup.scaleX != 1f || 
                    childGroup.scaleY != 1f || childGroup.translateX != 0f || childGroup.translateY != 0f
            
            if (hasTransforms || childGroup.name.isNotEmpty()) {
                builder.appendLine("$indentStr<group")
                if (childGroup.name.isNotEmpty()) {
                    builder.appendLine("$indentStr    android:name=\"${childGroup.name}\"")
                }
                if (childGroup.rotation != 0f) {
                    builder.appendLine("$indentStr    android:rotation=\"${childGroup.rotation}\"")
                }
                if (childGroup.pivotX != 0f) {
                    builder.appendLine("$indentStr    android:pivotX=\"${childGroup.pivotX}\"")
                }
                if (childGroup.pivotY != 0f) {
                    builder.appendLine("$indentStr    android:pivotY=\"${childGroup.pivotY}\"")
                }
                if (childGroup.scaleX != 1f) {
                    builder.appendLine("$indentStr    android:scaleX=\"${childGroup.scaleX}\"")
                }
                if (childGroup.scaleY != 1f) {
                    builder.appendLine("$indentStr    android:scaleY=\"${childGroup.scaleY}\"")
                }
                if (childGroup.translateX != 0f) {
                    builder.appendLine("$indentStr    android:translateX=\"${childGroup.translateX}\"")
                }
                if (childGroup.translateY != 0f) {
                    builder.appendLine("$indentStr    android:translateY=\"${childGroup.translateY}\"")
                }
                builder.appendLine("$indentStr>")
                appendGroup(builder, childGroup, indent + 1)
                builder.appendLine("$indentStr</group>")
            } else {
                appendGroup(builder, childGroup, indent)
            }
        }
    }

    private fun appendSvgGroup(builder: StringBuilder, group: VectorGroup, indent: Int) {
        val indentStr = "  ".repeat(indent)
        
        for (path in group.paths) {
            builder.append("$indentStr<path d=\"${path.pathData}\"")
            builder.append(" fill=\"${colorToHex(path.fillColor)}\"")
            if (path.strokeColor != null) {
                builder.append(" stroke=\"${colorToHex(path.strokeColor)}\"")
                builder.append(" stroke-width=\"${path.strokeWidth}\"")
            }
            if (path.fillAlpha < 1f) {
                builder.append(" fill-opacity=\"${path.fillAlpha}\"")
            }
            builder.appendLine("/>")
        }
        
        for (childGroup in group.groups) {
            val hasTransforms = childGroup.rotation != 0f || childGroup.scaleX != 1f || 
                    childGroup.scaleY != 1f || childGroup.translateX != 0f || childGroup.translateY != 0f
            
            if (hasTransforms) {
                val transforms = mutableListOf<String>()
                if (childGroup.translateX != 0f || childGroup.translateY != 0f) {
                    transforms.add("translate(${childGroup.translateX}, ${childGroup.translateY})")
                }
                if (childGroup.rotation != 0f) {
                    transforms.add("rotate(${childGroup.rotation}, ${childGroup.pivotX}, ${childGroup.pivotY})")
                }
                if (childGroup.scaleX != 1f || childGroup.scaleY != 1f) {
                    transforms.add("scale(${childGroup.scaleX}, ${childGroup.scaleY})")
                }
                builder.appendLine("$indentStr<g transform=\"${transforms.joinToString(" ")}\">")
                appendSvgGroup(builder, childGroup, indent + 1)
                builder.appendLine("$indentStr</g>")
            } else {
                appendSvgGroup(builder, childGroup, indent)
            }
        }
    }

    private fun appendComposePathsForGroup(builder: StringBuilder, group: VectorGroup, indent: Int) {
        val indentStr = "    ".repeat(indent)
        
        for (path in group.paths) {
            builder.appendLine("${indentStr}path(")
            builder.appendLine("$indentStr    fill = SolidColor(Color(${colorToArgbInt(path.fillColor)})),")
            if (path.strokeColor != null) {
                builder.appendLine("$indentStr    stroke = SolidColor(Color(${colorToArgbInt(path.strokeColor)})),")
                builder.appendLine("$indentStr    strokeLineWidth = ${path.strokeWidth}f,")
            }
            builder.appendLine("$indentStr) {")
            builder.appendLine("$indentStr    // Path data: ${path.pathData}")
            builder.appendLine("$indentStr}")
        }
        
        for (childGroup in group.groups) {
            appendComposePathsForGroup(builder, childGroup, indent)
        }
    }

    private fun extractViewBox(svg: String): Quadruple<Float, Float, Float, Float> {
        val viewBoxMatch = Regex("""viewBox\s*=\s*["']([^"']+)["']""").find(svg)
        return if (viewBoxMatch != null) {
            val parts = viewBoxMatch.groupValues[1].split(Regex("[\\s,]+")).map { it.toFloatOrNull() ?: 0f }
            if (parts.size >= 4) {
                Quadruple(parts[0], parts[1], parts[2], parts[3])
            } else {
                Quadruple(0f, 0f, 24f, 24f)
            }
        } else {
            Quadruple(0f, 0f, 24f, 24f)
        }
    }

    private fun extractAttribute(svg: String, attribute: String): String? {
        val regex = Regex("""$attribute\s*=\s*["']([^"']+)["']""")
        return regex.find(svg)?.groupValues?.get(1)
    }

    private fun extractPaths(svg: String): List<VectorPath> {
        val pathRegex = Regex("""<path[^>]*>""", RegexOption.DOT_MATCHES_ALL)
        return pathRegex.findAll(svg).mapNotNull { match ->
            val pathTag = match.value
            val d = extractAttribute(pathTag, "d") ?: return@mapNotNull null
            val fill = extractAttribute(pathTag, "fill")?.let { parseColor(it) } ?: Color.Black
            val stroke = extractAttribute(pathTag, "stroke")?.let { parseColor(it) }
            val strokeWidth = extractAttribute(pathTag, "stroke-width")?.toFloatOrNull() ?: 0f
            val fillOpacity = extractAttribute(pathTag, "fill-opacity")?.toFloatOrNull() ?: 1f
            
            VectorPath(
                id = UUID.randomUUID().toString(),
                pathData = d,
                fillColor = fill,
                strokeColor = stroke,
                strokeWidth = strokeWidth,
                fillAlpha = fillOpacity
            )
        }.toList()
    }

    private fun extractGroups(svg: String): List<VectorGroup> {
        return emptyList()
    }

    private fun parseColor(colorStr: String): Color {
        return try {
            when {
                colorStr.startsWith("#") -> {
                    val hex = colorStr.removePrefix("#")
                    when (hex.length) {
                        3 -> {
                            val r = hex[0].toString().repeat(2).toInt(16)
                            val g = hex[1].toString().repeat(2).toInt(16)
                            val b = hex[2].toString().repeat(2).toInt(16)
                            Color(r, g, b)
                        }
                        6 -> Color(android.graphics.Color.parseColor(colorStr))
                        8 -> Color(android.graphics.Color.parseColor(colorStr))
                        else -> Color.Black
                    }
                }
                colorStr == "none" -> Color.Transparent
                colorStr == "black" -> Color.Black
                colorStr == "white" -> Color.White
                colorStr == "red" -> Color.Red
                colorStr == "green" -> Color.Green
                colorStr == "blue" -> Color.Blue
                colorStr.startsWith("rgb") -> {
                    val match = Regex("""rgb\s*\(\s*(\d+)\s*,\s*(\d+)\s*,\s*(\d+)\s*\)""").find(colorStr)
                    if (match != null) {
                        val (r, g, b) = match.destructured
                        Color(r.toInt(), g.toInt(), b.toInt())
                    } else {
                        Color.Black
                    }
                }
                else -> Color.Black
            }
        } catch (e: Exception) {
            Color.Black
        }
    }

    private fun colorToHex(color: Color): String {
        val r = (color.red * 255).toInt()
        val g = (color.green * 255).toInt()
        val b = (color.blue * 255).toInt()
        val a = (color.alpha * 255).toInt()
        return if (a == 255) {
            String.format("#%02X%02X%02X", r, g, b)
        } else {
            String.format("#%02X%02X%02X%02X", a, r, g, b)
        }
    }

    private fun colorToArgbInt(color: Color): String {
        val a = (color.alpha * 255).toInt()
        val r = (color.red * 255).toInt()
        val g = (color.green * 255).toInt()
        val b = (color.blue * 255).toInt()
        return "0x${String.format("%02X%02X%02X%02X", a, r, g, b)}"
    }
}

data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
