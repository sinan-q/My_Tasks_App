package com.sinxn.mytasks.ui.features.folders

import com.sinxn.mytasks.data.local.entities.Folder

/**
 * A UI-specific model representing a folder to be displayed in a list.
 * This class is tailored for the UI and contains only the data needed for rendering the list item.
 */
data class FolderListItemUiModel(
    val id: Long,
    val name: String,
    val isLocked: Boolean,
)

/**
 * Mapper function to convert a [Folder] data entity to a [FolderListItemUiModel].
 * This transforms the raw data into a UI-friendly format for display.
 */
fun Folder.toListItemUiModel(): FolderListItemUiModel {
    return FolderListItemUiModel(
        id = this.folderId!!, // Assuming id is never null for a folder in a list
        name = this.name,
        isLocked = this.isLocked,
    )
}
