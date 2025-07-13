package com.jskaleel.fte.ui.screens.main.about.webpage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.jskaleel.fte.core.CallBack
import com.jskaleel.fte.ui.utils.ProvideAppBarNavigationIcon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebpageScreenRoute(
    onBack: CallBack,
    url: String,
) {
    ProvideAppBarNavigationIcon {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = "Back",
            )
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        WebpageScreenContent(
            url = url
        )
    }
}

