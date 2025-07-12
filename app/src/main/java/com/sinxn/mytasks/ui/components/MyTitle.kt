package com.sinxn.mytasks.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MyTitle(text: String, onClick: () -> Unit = {},) {
    RectangleCard(onClick = onClick, modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 5.dp)) {
        Text(text = text, style = MaterialTheme.typography.titleMedium)
    }
}