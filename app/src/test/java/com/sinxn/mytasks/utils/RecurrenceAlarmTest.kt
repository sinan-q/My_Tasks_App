package com.sinxn.mytasks.utils

import org.dmfs.rfc5545.recur.RecurrenceRule
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.TimeZone
import com.sinxn.mytasks.utils.toMillis


class RecurrenceAlarmTest {

    @Test
    fun testNextAlarmCalculation() {
        // Setup: A task repeats daily. Due date is today at 10:00 AM.
        val due = LocalDateTime.of(2026, 3, 22, 10, 0)
        val dueMillis = due.toMillis()

        // Reminder is 10 minutes before due
        val offsetMillis = 10 * 60 * 1000L
        val originalAlarmTime = dueMillis - offsetMillis

        val rule = RecurrenceRule("FREQ=DAILY")
        val iterator = rule.iterator(dueMillis, TimeZone.getDefault())

        // Simulate alarm firing slightly after its scheduled time
        val nowMillis = originalAlarmTime + 1000L 

        var nextAlarmTime = 0L

        while (iterator.hasNext()) {
            val occurrenceMillis = iterator.nextMillis()
            val potentialAlarmTime = occurrenceMillis - offsetMillis
            if (potentialAlarmTime > nowMillis) {
                nextAlarmTime = potentialAlarmTime
                break
            }
        }

        // Expected next alarm is tomorrow at 9:50 AM
        val expectedNextDue = due.plusDays(1)
        val expectedNextAlarmTime = expectedNextDue.toMillis() - offsetMillis

        assertEquals(expectedNextAlarmTime, nextAlarmTime)
    }
}
