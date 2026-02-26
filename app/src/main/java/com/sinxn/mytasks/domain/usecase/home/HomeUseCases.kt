package com.sinxn.mytasks.domain.usecase.home

import com.sinxn.mytasks.domain.models.Event
import com.sinxn.mytasks.domain.models.Folder
import com.sinxn.mytasks.domain.models.ItemType
import com.sinxn.mytasks.domain.models.Note
import com.sinxn.mytasks.domain.models.Pinned
import com.sinxn.mytasks.domain.models.Task
import com.sinxn.mytasks.ui.features.events.toListItemUiModel
import com.sinxn.mytasks.ui.features.folders.toListItemUiModel
import com.sinxn.mytasks.ui.features.home.HomeUiModel
import com.sinxn.mytasks.ui.features.notes.list.toListItemUiModel
import com.sinxn.mytasks.ui.features.tasks.list.toListItemUiModel
import java.time.LocalDateTime

data class HomeUseCases(
    val getDashboardData: GetDashboardDataUseCase
)

/**
 * Pure transformation use case that filters and organizes data for the home screen.
 * Takes all items as parameters to avoid duplicate database queries.
 * Performs in-memory filtering which is faster than separate DB queries.
 */
class GetDashboardDataUseCase {
    operator fun invoke(
        allFolders: List<Folder>,
        allEvents: List<Event>,
        allTasks: List<Task>,
        allNotes: List<Note>,
        pinnedItems: List<Pinned>,
        parentFolder: Folder?,
        parentFolderId: Long = 0L
    ): HomeUiModel {
        // Filter folders in the root/current directory
        val folders = allFolders
            .filter { it.parentFolderId == parentFolderId }
            .map { it.toListItemUiModel() }
        
        // Filter upcoming events: not archived, end > now, ordered by end ASC, limit 4
        val now = LocalDateTime.now()
        val upcomingEvents = allEvents
            .filter { !it.isArchived && it.end != null && it.end.isAfter(now) }
            .sortedBy { it.end }
            .take(4)
            .map { it.toListItemUiModel() }
        
        // Filter pending tasks: not archived, not completed, has due date, ordered by due ASC, limit 10
        val pendingTasks = allTasks
            .filter { !it.isArchived && !it.isCompleted && it.due != null }
            .sortedBy { it.due }
            .take(10)
            .map { it.toListItemUiModel() }
        
        // Filter notes in the current folder
        val notes = allNotes
            .filter { it.folderId == parentFolderId }
            .map { it.toListItemUiModel() }
        
        // Filter tasks in the current folder
        val tasks = allTasks
            .filter { it.folderId == parentFolderId }
            .map { it.toListItemUiModel() }
        
        // Resolve pinned items via in-memory lookup (eliminates N+1 query problem)
        val resolvedPinnedItems = pinnedItems.mapNotNull { pinned ->
            when (pinned.itemType) {
                ItemType.NOTE -> allNotes.find { it.id == pinned.itemId }?.toListItemUiModel()
                ItemType.TASK -> allTasks.find { it.id == pinned.itemId }?.toListItemUiModel()
                ItemType.EVENT -> allEvents.find { it.id == pinned.itemId }?.toListItemUiModel()
                ItemType.FOLDER -> allFolders.find { it.folderId == pinned.itemId }?.toListItemUiModel()
            }
        }
        
        return HomeUiModel(
            folders = folders,
            upcomingEvents = upcomingEvents,
            pendingTasks = pendingTasks,
            notes = notes,
            tasks = tasks,
            parentFolder = parentFolder,
            pinnedItems = resolvedPinnedItems
        )
    }
}
