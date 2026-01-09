package com.scto.codelikebastimove.features.soraeditor.model

import androidx.compose.ui.graphics.Color

data class EditorTheme(
    val name: String,
    val isDark: Boolean,
    val backgroundColor: Color,
    val foregroundColor: Color,
    val lineNumberColor: Color,
    val lineNumberBackgroundColor: Color,
    val currentLineColor: Color,
    val selectionColor: Color,
    val cursorColor: Color,
    val gutterDividerColor: Color,
    val syntaxColors: SyntaxColors
)

data class SyntaxColors(
    val keyword: Color,
    val type: Color,
    val string: Color,
    val number: Color,
    val comment: Color,
    val function: Color,
    val variable: Color,
    val operator: Color,
    val annotation: Color,
    val constant: Color,
    val attribute: Color,
    val tag: Color,
    val property: Color,
    val error: Color
)

object EditorThemes {
    
    val DarkModern = EditorTheme(
        name = "Dark Modern",
        isDark = true,
        backgroundColor = Color(0xFF1E1E1E),
        foregroundColor = Color(0xFFD4D4D4),
        lineNumberColor = Color(0xFF858585),
        lineNumberBackgroundColor = Color(0xFF1E1E1E),
        currentLineColor = Color(0xFF282828),
        selectionColor = Color(0xFF264F78),
        cursorColor = Color(0xFFAEAFAD),
        gutterDividerColor = Color(0xFF404040),
        syntaxColors = SyntaxColors(
            keyword = Color(0xFF569CD6),
            type = Color(0xFF4EC9B0),
            string = Color(0xFFCE9178),
            number = Color(0xFFB5CEA8),
            comment = Color(0xFF6A9955),
            function = Color(0xFFDCDCAA),
            variable = Color(0xFF9CDCFE),
            operator = Color(0xFFD4D4D4),
            annotation = Color(0xFFDCDCAA),
            constant = Color(0xFF4FC1FF),
            attribute = Color(0xFF9CDCFE),
            tag = Color(0xFF569CD6),
            property = Color(0xFF9CDCFE),
            error = Color(0xFFF44747)
        )
    )
    
    val LightModern = EditorTheme(
        name = "Light Modern",
        isDark = false,
        backgroundColor = Color(0xFFFFFFFF),
        foregroundColor = Color(0xFF000000),
        lineNumberColor = Color(0xFF237893),
        lineNumberBackgroundColor = Color(0xFFF3F3F3),
        currentLineColor = Color(0xFFFFF3CD),
        selectionColor = Color(0xFFADD6FF),
        cursorColor = Color(0xFF000000),
        gutterDividerColor = Color(0xFFE0E0E0),
        syntaxColors = SyntaxColors(
            keyword = Color(0xFF0000FF),
            type = Color(0xFF267F99),
            string = Color(0xFFA31515),
            number = Color(0xFF098658),
            comment = Color(0xFF008000),
            function = Color(0xFF795E26),
            variable = Color(0xFF001080),
            operator = Color(0xFF000000),
            annotation = Color(0xFF795E26),
            constant = Color(0xFF0070C1),
            attribute = Color(0xFF001080),
            tag = Color(0xFF800000),
            property = Color(0xFF001080),
            error = Color(0xFFE51400)
        )
    )
    
    val Dracula = EditorTheme(
        name = "Dracula",
        isDark = true,
        backgroundColor = Color(0xFF282A36),
        foregroundColor = Color(0xFFF8F8F2),
        lineNumberColor = Color(0xFF6272A4),
        lineNumberBackgroundColor = Color(0xFF282A36),
        currentLineColor = Color(0xFF44475A),
        selectionColor = Color(0xFF44475A),
        cursorColor = Color(0xFFF8F8F0),
        gutterDividerColor = Color(0xFF44475A),
        syntaxColors = SyntaxColors(
            keyword = Color(0xFFFF79C6),
            type = Color(0xFF8BE9FD),
            string = Color(0xFFF1FA8C),
            number = Color(0xFFBD93F9),
            comment = Color(0xFF6272A4),
            function = Color(0xFF50FA7B),
            variable = Color(0xFFF8F8F2),
            operator = Color(0xFFFF79C6),
            annotation = Color(0xFF50FA7B),
            constant = Color(0xFFBD93F9),
            attribute = Color(0xFF50FA7B),
            tag = Color(0xFFFF79C6),
            property = Color(0xFF8BE9FD),
            error = Color(0xFFFF5555)
        )
    )
    
    val MonokaiPro = EditorTheme(
        name = "Monokai Pro",
        isDark = true,
        backgroundColor = Color(0xFF2D2A2E),
        foregroundColor = Color(0xFFFCFCFA),
        lineNumberColor = Color(0xFF939293),
        lineNumberBackgroundColor = Color(0xFF2D2A2E),
        currentLineColor = Color(0xFF3E3B3F),
        selectionColor = Color(0xFF5B595C),
        cursorColor = Color(0xFFFCFCFA),
        gutterDividerColor = Color(0xFF5B595C),
        syntaxColors = SyntaxColors(
            keyword = Color(0xFFFF6188),
            type = Color(0xFF78DCE8),
            string = Color(0xFFFFD866),
            number = Color(0xFFAB9DF2),
            comment = Color(0xFF727072),
            function = Color(0xFFA9DC76),
            variable = Color(0xFFFCFCFA),
            operator = Color(0xFFFF6188),
            annotation = Color(0xFFA9DC76),
            constant = Color(0xFFAB9DF2),
            attribute = Color(0xFF78DCE8),
            tag = Color(0xFFFF6188),
            property = Color(0xFF78DCE8),
            error = Color(0xFFFF6188)
        )
    )
    
    val OneDarkPro = EditorTheme(
        name = "One Dark Pro",
        isDark = true,
        backgroundColor = Color(0xFF282C34),
        foregroundColor = Color(0xFFABB2BF),
        lineNumberColor = Color(0xFF4B5363),
        lineNumberBackgroundColor = Color(0xFF282C34),
        currentLineColor = Color(0xFF2C313C),
        selectionColor = Color(0xFF3E4451),
        cursorColor = Color(0xFF528BFF),
        gutterDividerColor = Color(0xFF3E4451),
        syntaxColors = SyntaxColors(
            keyword = Color(0xFFC678DD),
            type = Color(0xFFE5C07B),
            string = Color(0xFF98C379),
            number = Color(0xFFD19A66),
            comment = Color(0xFF5C6370),
            function = Color(0xFF61AFEF),
            variable = Color(0xFFE06C75),
            operator = Color(0xFF56B6C2),
            annotation = Color(0xFFE5C07B),
            constant = Color(0xFFD19A66),
            attribute = Color(0xFFD19A66),
            tag = Color(0xFFE06C75),
            property = Color(0xFFE06C75),
            error = Color(0xFFE06C75)
        )
    )
    
    val allThemes = listOf(DarkModern, LightModern, Dracula, MonokaiPro, OneDarkPro)
    
    fun getTheme(name: String): EditorTheme = allThemes.find { it.name == name } ?: DarkModern
}
