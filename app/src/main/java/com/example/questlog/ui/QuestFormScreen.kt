package com.example.questlog.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.questlog.data.Difficulty
import com.example.questlog.data.Quest
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

private fun formatFullDate(deadline: Long): String =
    SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        .apply { timeZone = TimeZone.getTimeZone("UTC") }
        .format(Date(deadline))

private fun todayUtcMillis(): Long {
    // Ambil tanggal lokal, lalu simpan sebagai tengah malam UTC (date-only).
    val local = Calendar.getInstance()
    val utc = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
        clear()
        set(
            local.get(Calendar.YEAR),
            local.get(Calendar.MONTH),
            local.get(Calendar.DAY_OF_MONTH)
        )
    }
    return utc.timeInMillis
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestFormScreen(
    questId: Int,
    onBack: () -> Unit,
    vm: QuestVM = viewModel()
) {
    var title by rememberSaveable { mutableStateOf("") }
    var difficulty by rememberSaveable { mutableStateOf(Difficulty.EASY.name) }
    var deadline by rememberSaveable { mutableStateOf(todayUtcMillis()) }
    var titleError by rememberSaveable { mutableStateOf(false) }
    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    var loaded by rememberSaveable { mutableStateOf(questId == 0) }
    var saving by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(questId) {
        if (!loaded) {
            vm.getQuest(questId)?.let { quest ->
                title = quest.title
                difficulty = quest.difficulty
                deadline = quest.deadline
            }
            loaded = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (questId == 0) "Tambah Quest" else "Edit Quest") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = {
                    title = it
                    if (it.isNotBlank()) titleError = false
                },
                label = { Text("Judul") },
                isError = titleError,
                supportingText = {
                    if (titleError) Text("Judul tidak boleh kosong")
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Column {
                Text("Difficulty", style = MaterialTheme.typography.labelLarge)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Difficulty.entries.forEach { option ->
                        FilterChip(
                            selected = difficulty == option.name,
                            onClick = { difficulty = option.name },
                            label = { Text(option.name) }
                        )
                    }
                }
            }

            Column {
                Text("Deadline", style = MaterialTheme.typography.labelLarge)
                Spacer(Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.DateRange, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(formatFullDate(deadline))
                }
            }

            Button(
                onClick = {
                    if (title.isBlank()) {
                        titleError = true
                    } else {
                        saving = true
                        vm.saveQuest(
                            Quest(
                                id = questId,
                                title = title.trim(),
                                difficulty = difficulty,
                                deadline = deadline
                            ),
                            onSaved = onBack
                        )
                    }
                },
                enabled = !saving,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Simpan")
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = deadline)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { deadline = it }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Batal")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
