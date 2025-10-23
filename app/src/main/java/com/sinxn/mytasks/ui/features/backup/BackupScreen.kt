package com.sinxn.mytasks.ui.features.backup

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.sinxn.mytasks.ui.components.RectangleButton
import kotlinx.coroutines.flow.collectLatest

@Composable
fun BackupScreen(
    viewModel: BackupViewModel = hiltViewModel()
) {

    val context = LocalContext.current
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.importDatabase(context,uri)
        }
    }

    val directoryPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri: Uri? ->
        uri?.let {
            viewModel.exportDatabase(context, it)
        }
    }

    LaunchedEffect(true) {
        viewModel.backupState.collectLatest { state ->
            Toast.makeText(context, state.toString(), Toast.LENGTH_SHORT).show()
        }
    }
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        RectangleButton(onClick = {
            directoryPickerLauncher.launch("backup.db")
            Toast.makeText(context, "Exported!" , Toast.LENGTH_SHORT).show()
        }) {
            Text("Export Database")
        }

        RectangleButton(onClick = {
            filePickerLauncher.launch("*/*")
        }) {
            Text("Import Database")
        }
    }
}