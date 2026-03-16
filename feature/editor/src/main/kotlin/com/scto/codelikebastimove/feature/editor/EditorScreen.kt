package com.scto.codelikebastimove.feature.editor

import android.content.Context
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.FormatIndentDecrease
import androidx.compose.material.icons.filled.FormatIndentIncrease
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material.icons.filled.WrapText
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.scto.codelikebastimove.core.resources.R
import com.scto.codelikebastimove.feature.editor.helpers.AutoBracketConfiguration
import com.scto.codelikebastimove.feature.soraeditor.model.EditorLanguageType
import io.github.rosemoe.sora.widget.CodeEditor
import io.github.rosemoe.sora.widget.EditorAutoCompleteWindow
import io.github.rosemoe.sora.widget.EditorEventListener
import io.github.rosemoe.sora.widget.SwitchPanel
import io.github.rosemoe.sora.widget.SymbolInputView
import io.github.rosemoe.sora.widget.component.EditorAutoCompletion
import io.github.rosemoe.sora.widget.component.EditorAutoPair
import io.github.rosemoe.sora.widget.component.Magnifier
import io.github.rosemoe.sora.widget.component.NativeAutoComplete
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme
import kotlinx.coroutines.launch
import androidx.compose.foundation.isSystemInDarkTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    filePath: String?, // Optional file path to load content from
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditorViewModel = viewModel()
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    val editorContent by viewModel.editorContent.collectAsState()
    val currentColorScheme by viewModel.currentColorScheme.collectAsState()
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    val hasSelection by viewModel.hasSelection.collectAsState()
    val isWordWrapEnabled by viewModel.isWordWrapEnabled.collectAsState()

    var showSearchBar by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var replaceQuery by remember { mutableStateOf("") }

    var showGoToLineDialog by remember { mutableStateOf(false) }
    var goToLineInput by remember { mutableStateOf("1") }

    var showMoreActionsBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    val isDarkTheme = isSystemInDarkTheme()

    // Initialize EditorUtils (Theme and Grammar registries)
    LaunchedEffect(Unit) {
        EditorUtils.initialize(context.applicationContext)
        if (currentColorScheme == null) {
            val defaultTheme = if (isDarkTheme) EditorUtils.ThemeRegistry.getDarkThemeName()
            else EditorUtils.ThemeRegistry.getLightThemeName()
            viewModel.setTheme(defaultTheme)
        }
        // If a file path is provided, try to determine language and load content (stub)
        filePath?.let {
            // For now, setting content to file path for demonstration
            // In a real app, you'd read the file content
            viewModel.updateContent("Content of file: $it")
            // Attempt to set language based on file extension
            viewModel.setLanguage(EditorLanguageType.fromFileName(it))
        }
    }

    // React to system theme changes
    LaunchedEffect(isDarkTheme) {
        val themeToApply = if (isDarkTheme) EditorUtils.ThemeRegistry.getDarkThemeName()
        else EditorUtils.ThemeRegistry.getLightThemeName()
        viewModel.setTheme(themeToApply)
    }

    if (showGoToLineDialog) {
        GoToLineDialog(
            currentLineText = goToLineInput,
            onLineTextChanged = { goToLineInput = it },
            onGoToLine = {
                viewModel.goToLine(goToLineInput.toIntOrNull() ?: 1)
                showGoToLineDialog = false
                goToLineInput = "1"
            },
            onDismiss = {
                showGoToLineDialog = false
                goToLineInput = "1"
            }
        )
    }

    if (showMoreActionsBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showMoreActionsBottomSheet = false },
            sheetState = sheetState
        ) {
            Column(modifier = Modifier.padding(vertical = 16.dp)) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.editor_action_cut)) },
                    onClick = {
                        viewModel.cut()
                        coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) showMoreActionsBottomSheet = false
                        }
                    },
                    leadingIcon = { Icon(Icons.Default.ContentCut, contentDescription = null) },
                    enabled = hasSelection
                )
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.editor_action_copy)) },
                    onClick = {
                        viewModel.copy()
                        coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) showMoreActionsBottomSheet = false
                        }
                    },
                    leadingIcon = { Icon(Icons.Default.ContentCopy, contentDescription = null) },
                    enabled = hasSelection
                )
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.editor_action_paste)) },
                    onClick = {
                        viewModel.paste()
                        coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) showMoreActionsBottomSheet = false
                        }
                    },
                    leadingIcon = { Icon(Icons.Default.ContentPaste, contentDescription = null) }
                )
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.editor_action_select_all)) },
                    onClick = {
                        viewModel.selectAll()
                        coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) showMoreActionsBottomSheet = false
                        }
                    },
                    leadingIcon = { Icon(Icons.Default.SelectAll, contentDescription = null) }
                )
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.editor_action_go_to_line)) },
                    onClick = {
                        coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                showMoreActionsBottomSheet = false
                                showGoToLineDialog = true
                            }
                        }
                    },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) } // Using search icon for "go to"
                )
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.editor_action_indent)) },
                    onClick = {
                        viewModel.indent()
                        coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) showMoreActionsBottomSheet = false
                        }
                    },
                    leadingIcon = { Icon(Icons.Default.FormatIndentIncrease, contentDescription = null) }
                )
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.editor_action_outdent)) },
                    onClick = {
                        viewModel.outdent()
                        coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) showMoreActionsBottomSheet = false
                        }
                    },
                    leadingIcon = { Icon(Icons.Default.FormatIndentDecrease, contentDescription = null) }
                )
                DropdownMenuItem(
                    text = { Text(if (isWordWrapEnabled) stringResource(R.string.editor_action_disable_word_wrap) else stringResource(R.string.editor_action_enable_word_wrap)) },
                    onClick = {
                        viewModel.toggleWordWrap()
                        coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) showMoreActionsBottomSheet = false
                        }
                    },
                    leadingIcon = { Icon(Icons.Default.WrapText, contentDescription = null) }
                )
                // Add more actions like "Change Language" here
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.editor_action_change_language)) },
                    onClick = {
                        // TODO: Implement language picker
                        println("Change language clicked!")
                        coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) showMoreActionsBottomSheet = false
                        }
                    },
                    leadingIcon = { Icon(Icons.Default.Language, contentDescription = null) }
                )
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Column {
                TopAppBar(
                    title = { Text(text = filePath?.substringAfterLast(File.separator) ?: stringResource(R.string.editor_screen_title)) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.undo() }) {
                            Icon(Icons.AutoMirrored.Filled.Undo, contentDescription = stringResource(R.string.editor_action_undo))
                        }
                        IconButton(onClick = { viewModel.redo() }) {
                            Icon(Icons.AutoMirrored.Filled.Redo, contentDescription = stringResource(R.string.editor_action_redo))
                        }
                        IconButton(onClick = { showSearchBar = !showSearchBar }) {
                            Icon(Icons.Default.Search, contentDescription = stringResource(R.string.editor_search))
                        }
                        IconButton(onClick = { showMoreActionsBottomSheet = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = stringResource(R.string.editor_more_actions))
                        }
                    }
                )
                AnimatedVisibility(
                    visible = showSearchBar,
                    enter = expandVertically(),
                    exit = shrinkVertically()
                ) {
                    SearchBar(
                        searchQuery = searchQuery,
                        onSearchQueryChange = { searchQuery = it },
                        onSearch = { viewModel.search(searchQuery) },
                        onClose = {
                            showSearchBar = false
                            searchQuery = ""
                            focusManager.clearFocus()
                        },
                        replaceQuery = replaceQuery,
                        onReplaceQueryChange = { replaceQuery = it },
                        onReplaceAll = { viewModel.replaceAll(replaceQuery) }
                    )
                }
            }
        },
        bottomBar = {
            // Optional: A bottom bar for status display (e.g., cursor line/column)
            // val cursorLine by viewModel.cursorLine.collectAsState()
            // val cursorColumn by viewModel.cursorColumn.collectAsState()
            // Text("Line: $cursorLine, Col: $cursorColumn", modifier = Modifier.fillMaxWidth().padding(8.dp))
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { androidContext ->
                    CodeEditor(androidContext).apply {
                        // Initial configuration
                        layoutParams = android.view.ViewGroup.LayoutParams(
                            android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                            android.view.ViewGroup.LayoutParams.MATCH_PARENT
                        )

                        // Enable components and fine-tuning
                        configureEditor(this, androidContext)

                        // Set the editor instance to ViewModel
                        viewModel.setEditor(this)

                        // Set text change listener to update ViewModel content
                        setTextChangeListener { text, _, _, _ ->
                            viewModel.updateContent(text.toString())
                        }

                        // Implement EditorEventListener
                        setEditorEventListener(object : EditorEventListener {
                            override fun onSelectionChanged(
                                editor: CodeEditor,
                                startLine: Int,
                                startColumn: Int,
                                endLine: Int,
                                endColumn: Int
                            ) {
                                viewModel.onSelectionChanged(editor.hasSelection())
                            }

                            override fun onScrollChanged(
                                editor: CodeEditor,
                                scrollX: Int,
                                scrollY: Int
                            ) {
                                // Optional: Update scroll position in ViewModel
                            }

                            override fun onKeyEvent(
                                editor: CodeEditor,
                                keyCode: Int,
                                event: android.view.KeyEvent
                            ): Boolean {
                                // Default handling of hardware keyboard keys is usually good.
                                // Return false to let the editor handle it.
                                return false
                            }

                            override fun onCursorChange(
                                editor: CodeEditor,
                                line: Int,
                                column: Int
                            ) {
                                viewModel.onCursorPositionChanged(line, column)
                            }
                        })
                    }
                },
                update = { editor ->
                    // React to ViewModel changes (Theme, Text, Language, Word Wrap)
                    if (editor.text.toString() != editorContent) {
                        editor.setText(editorContent)
                    }
                    currentColorScheme?.let {
                        if (editor.colorScheme != it) {
                            editor.colorScheme = it
                        }
                    }
                    currentLanguage.setup(editor) // Reapply language settings if it changes
                    if (editor.isWordWrap != isWordWrapEnabled) {
                        editor.setWordWrap(isWordWrapEnabled)
                    }
                }
            )
        }
    }
}

private fun configureEditor(editor: CodeEditor, context: Context) {
    editor.apply {
        // Line numbers
        setLineNumberEnabled(true)
        setPrintLineNumber(true)

        // Auto completion
        addComponents(NativeAutoComplete(editor))
        // Enable auto-completion on separators
        getComponent(EditorAutoCompletion::class.java).setAcceptOnSeparator(true)

        // Auto pair brackets/quotes
        addComponents(EditorAutoPair(editor))

        // Magnifier (text popup on long press)
        addComponents(Magnifier(editor))

        // Auto bracket/symbol pairs
        AutoBracketConfiguration.configure(this) // This might be redundant if EditorAutoPair handles it

        // Other configurations
        setEdgeEffectEnabled(true)
        // Adjusting EdgeEffectColor based on MaterialTheme
        setEdgeEffectColor(0xFFE0E0E0.toInt(), 0xFFE0E0E0.toInt()) // Light grey colors, consider dynamic theming
        setOverScrollEnabled(true)
        setHighlightCurrentLine(true)
        setHighlightCurrentBlock(true)
        setHighlightTextSelectionColor(0x80007ACC.toInt()) // VS Code selection blue
        setBlockLineColor(0xFF888888.toInt()) // Grey block line
        setDividerColor(0xFFBBBBBB.toInt()) // Light grey divider
        setShowDivider(true)
        setTabWidth(4) // Default tab width

        // Fine-tuning: Cursor and Line Spacing
        setCursorWidth(2.dp.value) // Example: 2dp cursor width
        setLineSpacing(1.2f, 0f) // Example: 1.2 line height multiplier

        // Example for showing a custom symbol input panel (optional)
        // val symbolInputView = SymbolInputView(context)
        // symbolInputView.addSymbols(arrayOf("->", "{", "}", "(", ")", "[", "]", ";", ",", ".", "=", "+", "-", "*", "/", "<", ">", "&", "|", "!", "?", ":", "~", "^", "%", "$", "#", "@", "`", "'", "\""))
        // addComponents(SwitchPanel(editor, symbolInputView, "Symbols"))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onClose: () -> Unit,
    replaceQuery: String,
    onReplaceQueryChange: (String) -> Unit,
    onReplaceAll: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                label = { Text(stringResource(R.string.editor_search)) },
                singleLine = true,
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {
                    onSearch()
                    focusManager.clearFocus()
                }),
                trailingIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, contentDescription = stringResource(R.string.close))
                    }
                }
            )
            IconButton(onClick = onSearch, modifier = Modifier.padding(start = 8.dp)) {
                Icon(Icons.Default.Search, contentDescription = stringResource(R.string.editor_search))
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = replaceQuery,
                onValueChange = onReplaceQueryChange,
                label = { Text(stringResource(R.string.editor_replace_with)) },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            Button(onClick = onReplaceAll, modifier = Modifier.padding(start = 8.dp)) {
                Text(stringResource(R.string.editor_replace_all))
            }
        }
    }
}

@Composable
private fun GoToLineDialog(
    currentLineText: String,
    onLineTextChanged: (String) -> Unit,
    onGoToLine: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.editor_go_to_line)) },
        text = {
            OutlinedTextField(
                value = currentLineText,
                onValueChange = { newValue ->
                    if (newValue.all { it.isDigit() } || newValue.isEmpty()) {
                        onLineTextChanged(newValue)
                    }
                },
                label = { Text(stringResource(R.string.line_number)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { onGoToLine() }),
                singleLine = true
            )
        },
        confirmButton = {
            Button(onClick = onGoToLine) {
                Text(stringResource(R.string.go_to))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}
