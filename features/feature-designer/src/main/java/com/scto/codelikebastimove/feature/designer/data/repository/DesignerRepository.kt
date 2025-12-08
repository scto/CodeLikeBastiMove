package com.scto.codelikebastimove.feature.designer.data.repository

import com.scto.codelikebastimove.feature.designer.data.model.BlockTree
import com.scto.codelikebastimove.feature.designer.data.model.ComponentDefinition
import com.scto.codelikebastimove.feature.designer.data.model.ComponentLibrary
import com.scto.codelikebastimove.feature.designer.data.model.DesignerProject
import com.scto.codelikebastimove.feature.designer.data.model.SourceBinding
import com.scto.codelikebastimove.feature.designer.data.model.ThemeDescriptor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class DesignerRepository {
    
    private val _projects = MutableStateFlow<List<DesignerProject>>(emptyList())
    val projects: Flow<List<DesignerProject>> = _projects.asStateFlow()
    
    private val _componentLibraries = MutableStateFlow<List<ComponentLibrary>>(emptyList())
    val componentLibraries: Flow<List<ComponentLibrary>> = _componentLibraries.asStateFlow()
    
    private val _savedThemes = MutableStateFlow<List<ThemeDescriptor>>(emptyList())
    val savedThemes: Flow<List<ThemeDescriptor>> = _savedThemes.asStateFlow()
    
    fun createProject(
        name: String,
        sourceBinding: SourceBinding? = null,
        themeDescriptor: ThemeDescriptor? = null
    ): DesignerProject {
        val project = DesignerProject(
            id = UUID.randomUUID().toString(),
            name = name,
            sourceBinding = sourceBinding,
            blockTree = BlockTree(name = name),
            themeDescriptor = themeDescriptor
        )
        _projects.value = _projects.value + project
        return project
    }
    
    fun updateProject(project: DesignerProject) {
        _projects.value = _projects.value.map {
            if (it.id == project.id) project.copy(modifiedAt = System.currentTimeMillis())
            else it
        }
    }
    
    fun deleteProject(projectId: String) {
        _projects.value = _projects.value.filter { it.id != projectId }
    }
    
    fun getProject(projectId: String): DesignerProject? {
        return _projects.value.find { it.id == projectId }
    }
    
    fun saveComponentLibrary(library: ComponentLibrary) {
        val existing = _componentLibraries.value.find { it.id == library.id }
        if (existing != null) {
            _componentLibraries.value = _componentLibraries.value.map {
                if (it.id == library.id) library else it
            }
        } else {
            _componentLibraries.value = _componentLibraries.value + library
        }
    }
    
    fun addComponentToLibrary(libraryId: String, component: ComponentDefinition) {
        _componentLibraries.value = _componentLibraries.value.map { library ->
            if (library.id == libraryId) {
                library.copy(
                    components = library.components + component
                )
            } else library
        }
    }
    
    fun removeComponentFromLibrary(libraryId: String, componentId: String) {
        _componentLibraries.value = _componentLibraries.value.map { library ->
            if (library.id == libraryId) {
                library.copy(
                    components = library.components.filter { it.id != componentId }
                )
            } else library
        }
    }
    
    fun getAllCustomComponents(): List<ComponentDefinition> {
        return _componentLibraries.value.flatMap { it.components }
    }
    
    fun saveTheme(theme: ThemeDescriptor) {
        val existing = _savedThemes.value.find { it.name == theme.name }
        if (existing != null) {
            _savedThemes.value = _savedThemes.value.map {
                if (it.name == theme.name) theme else it
            }
        } else {
            _savedThemes.value = _savedThemes.value + theme
        }
    }
    
    fun deleteTheme(themeName: String) {
        _savedThemes.value = _savedThemes.value.filter { it.name != themeName }
    }
    
    fun getTheme(themeName: String): ThemeDescriptor? {
        return _savedThemes.value.find { it.name == themeName }
    }
}

class ComponentLibraryRepository {
    
    private val _customComponents = MutableStateFlow<List<ComponentDefinition>>(emptyList())
    val customComponents: Flow<List<ComponentDefinition>> = _customComponents.asStateFlow()
    
    private val _userLibraries = MutableStateFlow<List<ComponentLibrary>>(emptyList())
    val userLibraries: Flow<List<ComponentLibrary>> = _userLibraries.asStateFlow()
    
    fun addComponent(component: ComponentDefinition) {
        _customComponents.value = _customComponents.value + component
    }
    
    fun updateComponent(component: ComponentDefinition) {
        _customComponents.value = _customComponents.value.map {
            if (it.id == component.id) component.copy(modifiedAt = System.currentTimeMillis())
            else it
        }
    }
    
    fun deleteComponent(componentId: String) {
        _customComponents.value = _customComponents.value.filter { it.id != componentId }
    }
    
    fun getComponent(componentId: String): ComponentDefinition? {
        return _customComponents.value.find { it.id == componentId }
    }
    
    fun getComponentsByCategory(category: com.scto.codelikebastimove.feature.designer.data.model.ComponentCategory): List<ComponentDefinition> {
        return _customComponents.value.filter { it.category == category }
    }
    
    fun createLibrary(name: String, description: String = ""): ComponentLibrary {
        val library = ComponentLibrary(
            name = name,
            description = description
        )
        _userLibraries.value = _userLibraries.value + library
        return library
    }
    
    fun exportComponentWithPrefix(
        component: ComponentDefinition,
        prefix: String
    ): String {
        return component.template.replace("{{PREFIX}}", prefix)
    }
}
