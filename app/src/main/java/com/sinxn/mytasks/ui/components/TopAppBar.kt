package com.sinxn.mytasks.ui.components

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import showBiometricsAuthentication

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTopAppBar(
    title: String,
    onNavigateUp: () -> Unit,
    showDeleteAction: Boolean,
    onDelete: () -> Unit = {}, // Default empty lambda if not needed
) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            IconButton(onClick = onNavigateUp) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
        actions = {
            if (showDeleteAction) {
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete"
                    )
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListTopAppBar(
    expanded: Boolean,
    setExpanded: (Boolean) -> Unit,
    hideLocked: Boolean,
    setHideLocked: (Boolean) -> Unit
) {
    val context = LocalContext.current
    fun authenticate(function: () -> Unit) {

        showBiometricsAuthentication(
            context,
            onSuccess = function,
            onError = { errString ->
                // Authentication error
                Toast.makeText(context, "Authentication error: $errString", Toast.LENGTH_SHORT).show()
            }
        )
    }
    TopAppBar(
        actions = {
            IconButton(
                onClick = { setExpanded(true) }
            ) {
                Icon(
                    Icons.Default.MoreVert,
                    contentDescription = "More Options",
                )
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { setExpanded(false) }
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp).clickable {
                        setExpanded(false)
                        if (hideLocked) authenticate({ setHideLocked(false) })
                        else setHideLocked(true)

                    },
                    text = (if (hideLocked) "Show" else "Hide") + " Locked Notes"
                )
            }
        },
        title = { Text("My Tasks") }
    )
}