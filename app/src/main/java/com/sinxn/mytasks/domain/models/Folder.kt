package com.sinxn.mytasks.domain.models

data class Folder(
    val folderId: Long = 0,
    val name: String = "",
    val parentFolderId: Long? = null,
    val isLocked: Boolean = false,
    val isArchived: Boolean = false,
)
