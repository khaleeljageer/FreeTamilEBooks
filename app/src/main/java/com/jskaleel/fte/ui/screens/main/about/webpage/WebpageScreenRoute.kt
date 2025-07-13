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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.jskaleel.fte.R
import com.jskaleel.fte.core.CallBack
import com.jskaleel.fte.ui.utils.ProvideAppBarNavigationIcon
import com.jskaleel.fte.ui.utils.ProvideAppBarTitle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebpageScreenRoute(
    onBack: CallBack,
    title: String,
    url: String,
) {
    ProvideAppBarTitle {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 20.sp
            ),
        )
    }
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

