package com.sinxn.mytasks.domain.models

data class Alarm(
    val alarmId: Long = 0,
    val isTask: Boolean,
    val taskId: Long,
    val time: Long,
    val trigger: com.sinxn.mytasks.utils.ReminderTrigger = com.sinxn.mytasks.utils.ReminderTrigger.FROM_END
)
