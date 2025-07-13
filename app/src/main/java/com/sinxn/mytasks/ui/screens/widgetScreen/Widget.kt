package com.sinxn.mytasks.ui.screens.widgetScreen

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.core.net.toUri
import androidx.glance.GlanceId
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.provideContent
import com.sinxn.mytasks.MainActivity
import com.sinxn.mytasks.R
import com.sinxn.mytasks.ui.navigation.Routes

class MyGlanceWidget : GlanceAppWidget() {
    override val sizeMode: SizeMode = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                WidgetContent()
            }
        }
    }
}
@Composable
fun WidgetContent() {
    val context = LocalContext.current

    fun getIntent(path: String) = Intent(Intent.ACTION_VIEW).apply {
        data = path.toUri()
        component = ComponentName(context.packageName, MainActivity::class.java.name)
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
    }

    ToolBarLayout(
        appName = "My Notes",
        appIconRes = R.drawable.laucher_ic_cropped ,
        headerButton = ToolBarButton(
            iconRes = R.drawable.forward_ic,
            contentDescription = "home",
            onClick = actionStartActivity(getIntent(Routes.Home.deepLink))
        ),
        buttons = listOf(
            ToolBarButton(
                iconRes = R.drawable.event_ic_add,
                contentDescription = "event",
                onClick = actionStartActivity(getIntent(Routes.Event.Add.deepLink))
            ),
            ToolBarButton(
                iconRes = R.drawable.task_ic_add,
                contentDescription = "task",
                onClick = actionStartActivity(getIntent(Routes.Task.Add.deepLink))
            ),
            ToolBarButton(
                iconRes = R.drawable.note_ic_add,
                contentDescription = "note",
                onClick = actionStartActivity(getIntent(Routes.Note.Add.deepLink))
            ),
        )
    )
}

class MyWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = MyGlanceWidget()
}


