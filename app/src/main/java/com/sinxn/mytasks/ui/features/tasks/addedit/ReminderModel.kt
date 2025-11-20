package com.sinxn.mytasks.ui.features.tasks.addedit

import com.sinxn.mytasks.utils.ReminderTrigger
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

data class ReminderModel(
    val duration: Int,
    val unit: ChronoUnit,
    val trigger: ReminderTrigger,
    val customDateTime: LocalDateTime? = null
)
