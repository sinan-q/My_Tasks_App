package com.sinxn.mytasks.domain.usecase.pinned

data class PinnedUseCases(
    val getPinnedItems: GetPinnedItems,
    val isPinned: IsPinned,
    val insertPinned: InsertPinned,
    val deletePinned: DeletePinned,
    val deletePinnedItems: DeletePinnedItems,
    val insertPinnedItems: InsertPinnedItems
)
