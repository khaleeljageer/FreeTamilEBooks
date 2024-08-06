package com.jskaleel.fte.ui.screens.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.jskaleel.fte.BuildConfig
import com.jskaleel.fte.R
import com.jskaleel.fte.core.model.ThemeConfig
import com.jskaleel.fte.core.utils.CallBack
import com.jskaleel.fte.ui.components.AppBarNoTitle
import com.jskaleel.fte.ui.extensions.screenPadding

@Composable
fun SettingsScreen(
    darkThemeConfig: ThemeConfig,
    onChangeDarkThemeConfig: (darkThemeConfig: ThemeConfig) -> Unit,
) {

    AppBarNoTitle() {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .screenPadding()
        ) {
            SettingsPanel(
                darkThemeConfig = darkThemeConfig,
                onChangeDarkThemeConfig = onChangeDarkThemeConfig,
            )
        }
    }
}


@Composable
private fun SettingsPanel(
    darkThemeConfig: ThemeConfig,
    onChangeDarkThemeConfig: (themeConfig: ThemeConfig) -> Unit,
) {
    SettingsSectionTitle(text = stringResource(R.string.title_theme))
    Column(Modifier.selectableGroup()) {
        SettingsThemeChooserRow(
            text = stringResource(R.string.str_system_default),
            selected = darkThemeConfig == ThemeConfig.FOLLOW_SYSTEM,
            onClick = { onChangeDarkThemeConfig(ThemeConfig.FOLLOW_SYSTEM) },
        )
        SettingsThemeChooserRow(
            text = stringResource(R.string.str_light),
            selected = darkThemeConfig == ThemeConfig.LIGHT,
            onClick = { onChangeDarkThemeConfig(ThemeConfig.LIGHT) },
        )
        SettingsThemeChooserRow(
            text = stringResource(R.string.str_dark),
            selected = darkThemeConfig == ThemeConfig.DARK,
            onClick = { onChangeDarkThemeConfig(ThemeConfig.DARK) },
        )
    }

    Divider(modifier = Modifier.padding(top = 16.dp))
    LinksPanel()
}

@Composable
fun LinksPanel() {
    Column(
        Modifier.fillMaxWidth(),
    ) {
        TextLink(
            text = stringResource(R.string.privacy_policy),
            url = PRIVACY_POLICY_URL,
        )
        Spacer(Modifier.width(16.dp))
        TextLink(
            text = stringResource(R.string.licenses),
            url = LICENSES_URL,
        )
        Spacer(Modifier.width(16.dp))
        TextLink(
            text = stringResource(R.string.feedback),
            url = FEEDBACK_URL,
        )
        Divider(modifier = Modifier.padding(top = 16.dp))
        Text(
            text = "App version: ${BuildConfig.VERSION_NAME}",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth(),
        )
    }
}

@Composable
private fun TextLink(text: String, url: String) {
    val launchResourceIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    val context = LocalContext.current

    Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .padding(vertical = 8.dp)
            .clickable {
                ContextCompat.startActivity(context, launchResourceIntent, null)
            },
    )
}

@Composable
private fun SettingsSectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
    )
}


@Composable
fun SettingsThemeChooserRow(
    text: String,
    selected: Boolean,
    onClick: CallBack,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .selectable(
                selected = selected,
                role = Role.RadioButton,
                onClick = onClick,
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(
            selected = selected,
            onClick = null,
        )
        Spacer(Modifier.width(8.dp))
        Text(text)
    }
}

private const val PRIVACY_POLICY_URL = "https://www.google.com"
private const val LICENSES_URL = "https://www.google.com"
private const val FEEDBACK_URL = "https://www.google.com"