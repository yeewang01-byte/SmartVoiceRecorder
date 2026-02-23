package com.smartvoice.recorder.ui

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.gap
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

// Extension function for easier spacing
fun ColumnScope.gap(space: androidx.compose.ui.unit.Dp = 12.dp) {
    Space(modifier = androidx.compose.foundation.layout.Spacer(
        modifier = androidx.compose.foundation.layout.height(space)
    ))
}

// Helper function - placeholder
@Composable
private fun Space(modifier: androidx.compose.foundation.layout.Spacer) {
    // Space already handled by Spacer in compose
}
