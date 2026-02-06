package com.scto.codelikebastimove.feature.soraeditor.model

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
  val syntaxColors: SyntaxColors,
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
  val error: Color,
  val parameter: Color,
  val punctuation: Color,
)

object EditorThemes {

  val DarkModern =
    EditorTheme(
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
      syntaxColors =
        SyntaxColors(
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
          error = Color(0xFFF44747),
          parameter = Color(0xFF9CDCFE),
          punctuation = Color(0xFFD4D4D4),
        ),
    )

  val LightModern =
    EditorTheme(
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
      syntaxColors =
        SyntaxColors(
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
          error = Color(0xFFE51400),
          parameter = Color(0xFF001080),
          punctuation = Color(0xFF000000),
        ),
    )

  val Dracula =
    EditorTheme(
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
      syntaxColors =
        SyntaxColors(
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
          error = Color(0xFFFF5555),
          parameter = Color(0xFFFFB86C),
          punctuation = Color(0xFFF8F8F2),
        ),
    )

  val MonokaiPro =
    EditorTheme(
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
      syntaxColors =
        SyntaxColors(
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
          error = Color(0xFFFF6188),
          parameter = Color(0xFFFC9867),
          punctuation = Color(0xFFFCFCFA),
        ),
    )

  val OneDarkPro =
    EditorTheme(
      name = "One Dark",
      isDark = true,
      backgroundColor = Color(0xFF282C34),
      foregroundColor = Color(0xFFABB2BF),
      lineNumberColor = Color(0xFF4B5363),
      lineNumberBackgroundColor = Color(0xFF282C34),
      currentLineColor = Color(0xFF2C313C),
      selectionColor = Color(0xFF3E4451),
      cursorColor = Color(0xFF528BFF),
      gutterDividerColor = Color(0xFF3E4451),
      syntaxColors =
        SyntaxColors(
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
          error = Color(0xFFE06C75),
          parameter = Color(0xFFE06C75),
          punctuation = Color(0xFFABB2BF),
        ),
    )

  val Darcula =
    EditorTheme(
      name = "Darcula",
      isDark = true,
      backgroundColor = Color(0xFF2B2B2B),
      foregroundColor = Color(0xFFA9B7C6),
      lineNumberColor = Color(0xFF606366),
      lineNumberBackgroundColor = Color(0xFF313335),
      currentLineColor = Color(0xFF323232),
      selectionColor = Color(0xFF214283),
      cursorColor = Color(0xFFBBBBBB),
      gutterDividerColor = Color(0xFF555758),
      syntaxColors =
        SyntaxColors(
          keyword = Color(0xFFCC7832),
          type = Color(0xFFA9B7C6),
          string = Color(0xFF6A8759),
          number = Color(0xFF6897BB),
          comment = Color(0xFF808080),
          function = Color(0xFFFFC66D),
          variable = Color(0xFFA9B7C6),
          operator = Color(0xFFA9B7C6),
          annotation = Color(0xFFBBB529),
          constant = Color(0xFF9876AA),
          attribute = Color(0xFFBABABA),
          tag = Color(0xFFE8BF6A),
          property = Color(0xFF9876AA),
          error = Color(0xFFBC3F3C),
          parameter = Color(0xFFA9B7C6),
          punctuation = Color(0xFFA9B7C6),
        ),
    )

  val QuietLight =
    EditorTheme(
      name = "QuietLight",
      isDark = false,
      backgroundColor = Color(0xFFF5F5F5),
      foregroundColor = Color(0xFF333333),
      lineNumberColor = Color(0xFF9B9B9B),
      lineNumberBackgroundColor = Color(0xFFEDEDED),
      currentLineColor = Color(0xFFE4F6D4),
      selectionColor = Color(0xFFC9D0D9),
      cursorColor = Color(0xFF54494B),
      gutterDividerColor = Color(0xFFD9D9D9),
      syntaxColors =
        SyntaxColors(
          keyword = Color(0xFF4B83CD),
          type = Color(0xFF7A3E9D),
          string = Color(0xFF448C27),
          number = Color(0xFFAB6526),
          comment = Color(0xFFAAA9A9),
          function = Color(0xFFAA3731),
          variable = Color(0xFF7A3E9D),
          operator = Color(0xFF777777),
          annotation = Color(0xFF8190A0),
          constant = Color(0xFFAB6526),
          attribute = Color(0xFFAB6526),
          tag = Color(0xFF4B83CD),
          property = Color(0xFF7A3E9D),
          error = Color(0xFFE05252),
          parameter = Color(0xFF7A3E9D),
          punctuation = Color(0xFF777777),
        ),
    )

  val GitHub =
    EditorTheme(
      name = "GitHub",
      isDark = false,
      backgroundColor = Color(0xFFFFFFFF),
      foregroundColor = Color(0xFF24292E),
      lineNumberColor = Color(0xFF959DA5),
      lineNumberBackgroundColor = Color(0xFFFAFBFC),
      currentLineColor = Color(0xFFFFFBDD),
      selectionColor = Color(0xFFC8E1FF),
      cursorColor = Color(0xFF24292E),
      gutterDividerColor = Color(0xFFE1E4E8),
      syntaxColors =
        SyntaxColors(
          keyword = Color(0xFFD73A49),
          type = Color(0xFF6F42C1),
          string = Color(0xFF032F62),
          number = Color(0xFF005CC5),
          comment = Color(0xFF6A737D),
          function = Color(0xFF6F42C1),
          variable = Color(0xFFE36209),
          operator = Color(0xFFD73A49),
          annotation = Color(0xFFE36209),
          constant = Color(0xFF005CC5),
          attribute = Color(0xFF005CC5),
          tag = Color(0xFF22863A),
          property = Color(0xFF005CC5),
          error = Color(0xFFCB2431),
          parameter = Color(0xFF24292E),
          punctuation = Color(0xFF24292E),
        ),
    )

  val SolarizedDark =
    EditorTheme(
      name = "Solarized Dark",
      isDark = true,
      backgroundColor = Color(0xFF002B36),
      foregroundColor = Color(0xFF839496),
      lineNumberColor = Color(0xFF586E75),
      lineNumberBackgroundColor = Color(0xFF002B36),
      currentLineColor = Color(0xFF073642),
      selectionColor = Color(0xFF073642),
      cursorColor = Color(0xFFD30102),
      gutterDividerColor = Color(0xFF073642),
      syntaxColors =
        SyntaxColors(
          keyword = Color(0xFF859900),
          type = Color(0xFFB58900),
          string = Color(0xFF2AA198),
          number = Color(0xFFD33682),
          comment = Color(0xFF586E75),
          function = Color(0xFF268BD2),
          variable = Color(0xFF268BD2),
          operator = Color(0xFF859900),
          annotation = Color(0xFF93A1A1),
          constant = Color(0xFFCB4B16),
          attribute = Color(0xFFB58900),
          tag = Color(0xFF268BD2),
          property = Color(0xFF268BD2),
          error = Color(0xFFDC322F),
          parameter = Color(0xFF839496),
          punctuation = Color(0xFF839496),
        ),
    )

  val SolarizedLight =
    EditorTheme(
      name = "Solarized Light",
      isDark = false,
      backgroundColor = Color(0xFFFDF6E3),
      foregroundColor = Color(0xFF657B83),
      lineNumberColor = Color(0xFF93A1A1),
      lineNumberBackgroundColor = Color(0xFFEEE8D5),
      currentLineColor = Color(0xFFEEE8D5),
      selectionColor = Color(0xFFEEE8D5),
      cursorColor = Color(0xFFD30102),
      gutterDividerColor = Color(0xFFEEE8D5),
      syntaxColors =
        SyntaxColors(
          keyword = Color(0xFF859900),
          type = Color(0xFFB58900),
          string = Color(0xFF2AA198),
          number = Color(0xFFD33682),
          comment = Color(0xFF93A1A1),
          function = Color(0xFF268BD2),
          variable = Color(0xFF268BD2),
          operator = Color(0xFF859900),
          annotation = Color(0xFF657B83),
          constant = Color(0xFFCB4B16),
          attribute = Color(0xFFB58900),
          tag = Color(0xFF268BD2),
          property = Color(0xFF268BD2),
          error = Color(0xFFDC322F),
          parameter = Color(0xFF657B83),
          punctuation = Color(0xFF657B83),
        ),
    )

  val Nord =
    EditorTheme(
      name = "Nord",
      isDark = true,
      backgroundColor = Color(0xFF2E3440),
      foregroundColor = Color(0xFFD8DEE9),
      lineNumberColor = Color(0xFF4C566A),
      lineNumberBackgroundColor = Color(0xFF2E3440),
      currentLineColor = Color(0xFF3B4252),
      selectionColor = Color(0xFF434C5E),
      cursorColor = Color(0xFFD8DEE9),
      gutterDividerColor = Color(0xFF434C5E),
      syntaxColors =
        SyntaxColors(
          keyword = Color(0xFF81A1C1),
          type = Color(0xFF8FBCBB),
          string = Color(0xFFA3BE8C),
          number = Color(0xFFB48EAD),
          comment = Color(0xFF616E88),
          function = Color(0xFF88C0D0),
          variable = Color(0xFFD8DEE9),
          operator = Color(0xFF81A1C1),
          annotation = Color(0xFFD08770),
          constant = Color(0xFFB48EAD),
          attribute = Color(0xFF8FBCBB),
          tag = Color(0xFF81A1C1),
          property = Color(0xFF88C0D0),
          error = Color(0xFFBF616A),
          parameter = Color(0xFFD8DEE9),
          punctuation = Color(0xFFECEFF4),
        ),
    )

  val Monokai =
    EditorTheme(
      name = "Monokai",
      isDark = true,
      backgroundColor = Color(0xFF272822),
      foregroundColor = Color(0xFFF8F8F2),
      lineNumberColor = Color(0xFF90908A),
      lineNumberBackgroundColor = Color(0xFF272822),
      currentLineColor = Color(0xFF3E3D32),
      selectionColor = Color(0xFF49483E),
      cursorColor = Color(0xFFF8F8F0),
      gutterDividerColor = Color(0xFF49483E),
      syntaxColors =
        SyntaxColors(
          keyword = Color(0xFFF92672),
          type = Color(0xFF66D9EF),
          string = Color(0xFFE6DB74),
          number = Color(0xFFAE81FF),
          comment = Color(0xFF75715E),
          function = Color(0xFFA6E22E),
          variable = Color(0xFFF8F8F2),
          operator = Color(0xFFF92672),
          annotation = Color(0xFFA6E22E),
          constant = Color(0xFFAE81FF),
          attribute = Color(0xFF66D9EF),
          tag = Color(0xFFF92672),
          property = Color(0xFF66D9EF),
          error = Color(0xFFF92672),
          parameter = Color(0xFFFD971F),
          punctuation = Color(0xFFF8F8F2),
        ),
    )

  val allThemes = listOf(
    Darcula, QuietLight, Monokai, GitHub, SolarizedDark, SolarizedLight,
    OneDarkPro, Dracula, Nord, DarkModern, LightModern, MonokaiPro,
  )

  fun getTheme(name: String): EditorTheme = allThemes.find { it.name == name } ?: DarkModern
}
