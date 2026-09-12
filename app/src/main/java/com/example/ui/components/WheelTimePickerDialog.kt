package com.example.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import java.util.Calendar

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WheelPicker(
    count: Int,
    initialIndex: Int,
    onItemSelected: (Int) -> Unit,
    itemContent: @Composable (index: Int, isSelected: Boolean) -> Unit
) {
    val itemHeight = 48.dp
    // Start at a large multiple of count to allow infinite scrolling in both directions
    val initialCenter = (Int.MAX_VALUE / 2 / count) * count + initialIndex
    // We want the initialCenter to be exactly in the middle.
    // If the list shows 3 items, the first visible index should be initialCenter - 1
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialCenter - 1)
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
    
    val centerIndex by remember { 
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            if (layoutInfo.visibleItemsInfo.isEmpty()) return@derivedStateOf null
            val center = layoutInfo.viewportStartOffset + layoutInfo.viewportSize.height / 2
            layoutInfo.visibleItemsInfo.minByOrNull { kotlin.math.abs((it.offset + it.size / 2) - center) }?.index
        }
    }

    LaunchedEffect(centerIndex) {
        centerIndex?.let {
            onItemSelected(it % count)
        }
    }

    LazyColumn(
        state = listState,
        flingBehavior = flingBehavior,
        modifier = Modifier
            .height(itemHeight * 3)
            .width(64.dp)
    ) {
        items(
            count = Int.MAX_VALUE,
            key = null
        ) { index ->
            val isSelected = index == centerIndex
            Box(
                modifier = Modifier
                    .height(itemHeight)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                itemContent(index % count, isSelected)
            }
        }
    }
}

@Composable
fun WheelTimePickerDialog(
    initialHourOfDay: Int,
    initialMinute: Int,
    onDismissRequest: () -> Unit,
    onTimeSelected: (hourOfDay: Int, minute: Int) -> Unit
) {
    var selectedHour12 by remember { mutableStateOf(if (initialHourOfDay % 12 == 0) 12 else initialHourOfDay % 12) }
    var selectedMinute by remember { mutableStateOf(initialMinute) }
    var isAm by remember { mutableStateOf(initialHourOfDay < 12) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = {
                val finalHourOfDay = when {
                    isAm && selectedHour12 == 12 -> 0
                    !isAm && selectedHour12 < 12 -> selectedHour12 + 12
                    else -> selectedHour12
                }
                onTimeSelected(finalHourOfDay, selectedMinute)
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancel")
            }
        },
        title = {
            Text("Select Time", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
        },
        text = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                // Background highlight for the centered row
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Hour Picker
                    WheelPicker(
                        count = 12,
                        initialIndex = selectedHour12 - 1,
                        onItemSelected = { selectedHour12 = it + 1 }
                    ) { index, isSelected ->
                        val value = index + 1
                        Text(
                            text = String.format("%02d", value),
                            fontSize = if (isSelected) 24.sp else 20.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.alpha(if (isSelected) 1f else 0.5f)
                        )
                    }

                    Text(
                        text = ":",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    // Minute Picker
                    WheelPicker(
                        count = 60,
                        initialIndex = selectedMinute,
                        onItemSelected = { selectedMinute = it }
                    ) { index, isSelected ->
                        Text(
                            text = String.format("%02d", index),
                            fontSize = if (isSelected) 24.sp else 20.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.alpha(if (isSelected) 1f else 0.5f)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // AM/PM Selector
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.height(48.dp * 3)
                    ) {
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = "AM",
                            fontSize = if (isAm) 20.sp else 16.sp,
                            fontWeight = if (isAm) FontWeight.Bold else FontWeight.Normal,
                            color = if (isAm) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .alpha(if (isAm) 1f else 0.5f)
                                .clickable { isAm = true }
                                .padding(8.dp)
                        )
                        Text(
                            text = "PM",
                            fontSize = if (!isAm) 20.sp else 16.sp,
                            fontWeight = if (!isAm) FontWeight.Bold else FontWeight.Normal,
                            color = if (!isAm) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .alpha(if (!isAm) 1f else 0.5f)
                                .clickable { isAm = false }
                                .padding(8.dp)
                        )
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    )
}
