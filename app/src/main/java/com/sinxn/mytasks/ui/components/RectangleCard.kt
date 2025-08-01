package com.sinxn.mytasks.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun RectangleCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    colors: CardColors = CardDefaults.cardColors(containerColor = Color.Transparent),
    content: @Composable (ColumnScope.() -> Unit)
) {
    Card(
        modifier = Modifier
           // .fillMaxWidth()
            .padding(1.dp)
            //.border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.7f), RoundedCornerShape(4.dp))
            .clickable {
                if (onClick != null) {
                    onClick()
                }
            }.then(modifier),
        colors = colors,
        shape = RoundedCornerShape(4.dp)
    ) {
        content()
    }
}