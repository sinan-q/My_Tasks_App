package com.sinxn.mytasks.ui.screens.widgetScreen

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.size
import com.sinxn.mytasks.MainActivity

class MyGlanceWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            MyGlanceWidgetContent()
        }
    }
}

@Composable
fun MyGlanceWidgetContent() {
    val context = LocalContext.current

    val intent = Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse("mytasks://add_note")
        component = ComponentName(context.packageName, MainActivity::class.java.name)
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
    }

    val launchIntent = actionStartActivity(intent)

    Column(
        modifier = GlanceModifier
                .size(100.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            ImageProvider(android.R.drawable.ic_delete),
            contentDescription = null,
        )
    }
}

class MyWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = MyGlanceWidget()
}


