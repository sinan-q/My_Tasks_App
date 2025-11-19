package com.sinxn.mytasks.ui.features.folders

import com.sinxn.mytasks.domain.models.Folder

/**
 * A UI-specific model representing a folder to be displayed in a list.
 * This class is tailored for the UI and contains only the data needed for rendering the list item.
 */
data class FolderListItemUiModel(
    val id: Long,
    val name: String,
    val isLocked: Boolean,
    val isSelected: Boolean = false,
)

/**
 * Mapper function to convert a [Folder] data entity to a [FolderListItemUiModel].
 * This transforms the raw data into a UI-friendly format for display.
 */
fun Folder.toListItemUiModel(): FolderListItemUiModel {
    return FolderListItemUiModel(
        id = this.folderId, // Domain model uses non-null folderId
        name = this.name,
        isLocked = this.isLocked,
    )
}
