package com.sinxn.mytasks.utils

import java.time.temporal.ChronoUnit

enum class ReminderTypes(val unit: ChronoUnit) {
    MINUTE(ChronoUnit.MINUTES),
    HOUR(ChronoUnit.HOURS),
    DAY(ChronoUnit.DAYS),
    WEEK(ChronoUnit.WEEKS)
}
