package com.sinxn.mytasks.ui.components

import androidx.compose.foundation.border
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

@Composable
fun RectangleFAB(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    shape: Shape = RectangleShape,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    content: @Composable () -> Unit
) {
    FloatingActionButton(
        modifier = modifier.border(1.dp, MaterialTheme.colorScheme.outline, shape),
        onClick = onClick,
        shape = shape,
        containerColor = containerColor,
        contentColor = contentColor,
        content = content
    )
}

@Composable
fun ExtendedRectangleFAB(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    text: @Composable () -> Unit,
    shape: Shape = RectangleShape,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
) {
    ExtendedFloatingActionButton(
        modifier = modifier.border(1.dp, MaterialTheme.colorScheme.outline, shape),
        onClick = onClick,
        icon = icon,
        text = text,
        shape = shape,
        containerColor = containerColor,
        contentColor = contentColor,
    )
}