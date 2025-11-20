package com.sinxn.mytasks.utils

object Constants {
    const val SAVE_SUCCESS = "Item Saved Successfully"
    const val UPDATE_SUCCESS = "Item Updated Successfully"
    const val DELETE_SUCCESS = "Item Deleted Successfully"
    const val DELETE_FAILED = "Item Deletion Failed"

    const val NOT_FOUND = "Item Not Found"

    const val SAVE_FAILED_EMPTY = "Note cannot be empty"

    const val EVENT_SAVE_FAILED_END_AFTER_START = "Start date cannot be after end date"
    const val EVENT_SAVE_FAILED_DATE_EMPTY = "Start date or end date cannot be empty"
    const val NOTE_SAVE_FAILED_NO_DUE = "Cannot set reminder for a note without a due date"
    const val NOTE_SAVE_FAILED_REMINDER_IN_PAST = "Reminder should be in a future time"

    const val TASK_REMINDER_ALREADY_EXISTS = "Reminder already exists"
}