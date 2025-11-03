package com.sinxn.mytasks.di

import com.sinxn.mytasks.data.repository.AlarmRepository
import com.sinxn.mytasks.data.repository.EventRepository
import com.sinxn.mytasks.data.repository.ExpiredTaskRepository
import com.sinxn.mytasks.data.repository.FolderRepository
import com.sinxn.mytasks.data.repository.NoteRepository
import com.sinxn.mytasks.data.repository.PinnedRepository
import com.sinxn.mytasks.data.repository.TaskRepository
import com.sinxn.mytasks.domain.repository.AlarmRepositoryInterface
import com.sinxn.mytasks.domain.repository.EventRepositoryInterface
import com.sinxn.mytasks.domain.repository.ExpiredTaskRepositoryInterface
import com.sinxn.mytasks.domain.repository.FolderRepositoryInterface
import com.sinxn.mytasks.domain.repository.NoteRepositoryInterface
import com.sinxn.mytasks.domain.repository.PinnedRepositoryInterface
import com.sinxn.mytasks.domain.repository.TaskRepositoryInterface
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindTaskRepository(impl: TaskRepository): TaskRepositoryInterface

    @Binds
    abstract fun bindNoteRepository(impl: NoteRepository): NoteRepositoryInterface

    @Binds
    abstract fun bindFolderRepository(impl: FolderRepository): FolderRepositoryInterface

    @Binds
    abstract fun bindEventRepository(impl: EventRepository): EventRepositoryInterface

    @Binds
    abstract fun bindAlarmRepository(impl: AlarmRepository): AlarmRepositoryInterface

    @Binds
    abstract fun bindPinnedRepository(impl: PinnedRepository): PinnedRepositoryInterface

    @Binds
    abstract fun bindExpiredTaskRepository(impl: ExpiredTaskRepository): ExpiredTaskRepositoryInterface

}
