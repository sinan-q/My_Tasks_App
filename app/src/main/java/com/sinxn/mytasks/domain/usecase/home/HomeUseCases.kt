package com.sinxn.mytasks.domain.usecase.home

import com.sinxn.mytasks.domain.models.ItemType
import com.sinxn.mytasks.domain.repository.EventRepositoryInterface
import com.sinxn.mytasks.domain.repository.FolderRepositoryInterface
import com.sinxn.mytasks.domain.repository.NoteRepositoryInterface
import com.sinxn.mytasks.domain.repository.PinnedRepositoryInterface
import com.sinxn.mytasks.domain.repository.TaskRepositoryInterface
import com.sinxn.mytasks.ui.features.events.toListItemUiModel
import com.sinxn.mytasks.ui.features.folders.toListItemUiModel
import com.sinxn.mytasks.ui.features.home.HomeUiModel
import com.sinxn.mytasks.ui.features.notes.list.toListItemUiModel
import com.sinxn.mytasks.ui.features.tasks.list.toListItemUiModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data class HomeUseCases(
    val getDashboardData: GetDashboardDataUseCase
)

class GetDashboardDataUseCase(
    private val folderRepository: FolderRepositoryInterface,
    private val eventRepository: EventRepositoryInterface,
    private val taskRepository: TaskRepositoryInterface,
    private val noteRepository: NoteRepositoryInterface,
    private val pinnedRepository: PinnedRepositoryInterface
) {
    suspend operator fun invoke(): Flow<HomeUiModel> {
        val parentFolder = folderRepository.getFolderById(0L)
        return combine(
            folderRepository.getSubFolders(0).map { folders -> folders.map { it.toListItemUiModel() } },
            eventRepository.getUpcomingEvents(4).map { events -> events.map { it.toListItemUiModel() } },
            taskRepository.getTasksWithDueDate(10).map { tasks -> tasks.map { it.toListItemUiModel() } },
            noteRepository.getNotesByFolderId(0).map { notes -> notes.map { it.toListItemUiModel() } },
            taskRepository.getTasksByFolderId(0).map { tasks -> tasks.map { it.toListItemUiModel() } },
            pinnedRepository.getPinnedItems().map { pinnedList ->
                pinnedList.map { pinned ->
                    when (pinned.itemType) {
                        ItemType.NOTE -> noteRepository.getNoteById(pinned.itemId)?.toListItemUiModel()
                        ItemType.TASK -> taskRepository.getTaskById(pinned.itemId)?.toListItemUiModel()
                        ItemType.EVENT -> eventRepository.getEventById(pinned.itemId)!!.toListItemUiModel()
                        ItemType.FOLDER -> folderRepository.getFolderById(pinned.itemId)?.toListItemUiModel()
                    }
                }
            },
        ) { folders, upcomingEvents, pendingTasks, notes, tasks, pinned ->
            HomeUiModel(
                folders = folders,
                upcomingEvents = upcomingEvents,
                pendingTasks = pendingTasks,
                notes = notes,
                tasks = tasks,
                parentFolder = parentFolder,
                pinnedItems = pinned.filterNotNull()
            )
        }
    }

}

@Suppress("UNCHECKED_CAST")
inline fun <T1, T2, T3, T4, T5, T6, R> combine(
    flow: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>,
    flow4: Flow<T4>,
    flow5: Flow<T5>,
    flow6: Flow<T6>,
    crossinline transform: suspend (T1, T2, T3, T4, T5, T6) -> R
): Flow<R> {
    return kotlinx.coroutines.flow.combine(listOf(flow, flow2, flow3, flow4, flow5, flow6)) { args ->
        transform(
            args[0] as T1,
            args[1] as T2,
            args[2] as T3,
            args[3] as T4,
            args[4] as T5,
            args[5] as T6,
        )
    }
}
