package com.scto.codelikebastimove.feature.editor

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.scto.codelikebastimove.core.resources.R
import com.scto.codelikebastimove.feature.editor.helpers.AutoBracketConfiguration
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry
import io.github.rosemoe.sora.widget.CodeEditor
import io.github.rosemoe.sora.widget.EditorAutoCompleteWindow
import io.github.rosemoe.sora.widget.SwitchPanel
import io.github.rosemoe.sora.widget.SymbolInputView
import io.github.rosemoe.sora.widget.component.Magnifier
import io.github.rosemoe.sora.widget.component.NativeAutoComplete
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme

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

    var showSearchBar by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var replaceQuery by remember { mutableStateOf("") }

    // Initialize EditorUtils (Theme and Grammar registries)
    LaunchedEffect(Unit) {
        EditorUtils.initialize(context.applicationContext)
        if (currentColorScheme == null) {
            // Set a default theme if none is set (e.g., "darcula")
            viewModel.setTheme("darcula")
        }
        // If a file path is provided, try to determine language and load content (stub)
        filePath?.let {
            // For now, setting content to file path for demonstration
            // In a real app, you'd read the file content
            viewModel.updateContent("Content of file: $it")
            // Attempt to set language based on file extension
            viewModel.setLanguage(EditorLanguage.fromFileName(it))
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
                        // Add more actions as needed
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

                        // Enable components
                        configureEditor(this, androidContext)

                        // Set the editor instance to ViewModel
                        viewModel.setEditor(this)

                        // Set text change listener to update ViewModel content
                        setTextChangeListener { text, _, _, _ ->
                            viewModel.updateContent(text.toString())
                        }
                    }
                },
                update = { editor ->
                    // React to ViewModel changes (Theme, Text, Language)
                    if (editor.text.toString() != editorContent) {
                        editor.setText(editorContent)
                    }
                    currentColorScheme?.let {
                        if (editor.colorScheme != it) {
                            editor.colorScheme = it
                        }
                    }
                    currentLanguage.setup(editor) // Reapply language settings if it changes
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
        editor.addComponents(NativeAutoComplete(editor))
        // Or for custom autocompletion: editor.autoCompleteWindow = EditorAutoCompleteWindow(editor)

        // Magnifier (text popup on long press)
        addComponents(Magnifier(editor))

        // Auto bracket/symbol pairs
        AutoBracketConfiguration.configure(this)

        // Other configurations
        setEdgeEffectEnabled(true)
        setEdgeEffectColor(0xFFE0E0E0.toInt(), 0xFFE0E0E0.toInt()) // Light grey colors
        setOverScrollEnabled(true)
        setHighlightCurrentLine(true)
        setHighlightCurrentBlock(true)
        setHighlightTextSelectionColor(0x80007ACC.toInt()) // VS Code selection blue
        setBlockLineColor(0xFF888888.toInt()) // Grey block line
        setDividerColor(0xFFBBBBBB.toInt()) // Light grey divider
        setShowDivider(true)
        setTabWidth(4) // Default tab width

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
        Row(modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
            OutlinedTextField(
                value = replaceQuery,
                onValueChange = onReplaceQueryChange,
                label = { Text("Replace with") }, // TODO: Add string resource for "Replace with"
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            Button(onClick = onReplaceAll, modifier = Modifier.padding(start = 8.dp)) {
                Text("Replace All") // TODO: Add string resource for "Replace All"
            }
        }
    }
}
