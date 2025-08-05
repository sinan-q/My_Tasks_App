package com.sinxn.mytasks.ui.components

import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun <T> ScrollablePicker(
    modifier: Modifier = Modifier,
    values: List<T>,
    defaultValue: T,
    height: Dp = 36.dp,
    fontStyle: TextStyle = TextStyle.Default,
    onValueChange: (T) -> Unit
) {
    // The size of each item and the total height of the picker
    val itemHeight = height
    val totalHeight = itemHeight // Show 5 items at a time

    // The LazyListState to control and observe the scrolling
    val lazyListState = rememberLazyListState(
        initialFirstVisibleItemIndex = (values.indexOf(defaultValue)).coerceIn(0, values.size - 1)
    )

    // A snapping behavior for the LazyColumn
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = lazyListState)

    // This effect listens to the scroll position and updates the selected value
    LaunchedEffect(lazyListState) {
        snapshotFlow { lazyListState.firstVisibleItemIndex }
            .distinctUntilChanged()
            .collect { index ->
                // Calculate the value based on the centered item
                // The +2 offset is because we are showing 5 items, and the 3rd one is the center
                val selectedValue = values[index]
                onValueChange(selectedValue)
            }
    }

    RectangleCard (
        modifier = modifier
    ) {
        LazyColumn(
            state = lazyListState,
            flingBehavior = flingBehavior,

        ) {

            items(values.size) { index ->
                val currentValue = values[index]
                val isCentered = (remember { derivedStateOf { lazyListState.firstVisibleItemIndex } }).value == index
                Column(
                    modifier = Modifier.height(itemHeight).fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = currentValue.toString(),
                        textAlign = TextAlign.Center,
                        style = fontStyle.copy(
                            color = if (isCentered) LocalContentColor.current else LocalContentColor.current.copy(alpha = 0.4f)
                        )
                    )
                }

            }
        }

        // A simple overlay to highlight the selected area
        Box(
            modifier = Modifier
                    .alpha(0.2f) // Example styling
        )
    }
}

@Preview
@Composable
fun PreviewScrollable() {

    ScrollablePicker(
        values = listOf("Item 1", "Item 2", "Item 3", "Item 4", "Item 5"),
        defaultValue = "Item 1",
        onValueChange = {}
    )
}