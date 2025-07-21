package com.jskaleel.fte.ui.screens.main.about

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jskaleel.fte.core.CallBack
import com.jskaleel.fte.core.model.ImageType
import com.jskaleel.fte.core.model.getImagePainter
import com.jskaleel.fte.ui.theme.FTEBooksTheme
import com.jskaleel.fte.ui.theme.customColors

@Composable
fun AboutScreenContent(
    menus: List<AboutUiModel>,
    onEvent: (AboutEvent) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
    ) {
        items(
            items = menus,
            key = { it.title }
        ) { menu ->
            Text(
                text = menu.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            menu.items.forEach { item ->
                AboutMenuItem(
                    onClick = {
                        onEvent(
                            AboutEvent.ItemClicked(
                                title = item.title,
                                type = item.type
                            )
                        )
                    },
                    title = item.title,
                    icon = item.icon,
                    description = item.description,
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun AboutMenuItem(
    onClick: CallBack,
    title: String,
    description: String,
    icon: ImageType
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onClick()
                }
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.width(1.dp))
            Icon(
                modifier = Modifier
                    .size(30.dp)
                    .background(color = MaterialTheme.colorScheme.primary, shape = CircleShape)
                    .clip(CircleShape)
                    .padding(6.dp),
                painter = icon.getImagePainter(),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.width(6.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.customColors.textPrimary
                )
                if (description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = description,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.customColors.textSecondary
                    )
                }
            }

            Icon(
                imageVector = Icons.Rounded.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AboutScreenContentPreview() {
    FTEBooksTheme {
        val menus = listOf(
            AboutUiModel(
                title = "Section 1",
                items = listOf(
                    AboutItemUiModel(
                        title = "Item 1.1",
                        description = "Hello",
                        type = Type.None,
                        icon = ImageType.EMPTY
                    ),
                    AboutItemUiModel(
                        title = "Item 1.2",
                        description = "Hello",
                        type = Type.Url("https://example.com"),
                        icon = ImageType.EMPTY
                    )
                )
            ),
            AboutUiModel(
                title = "Section 2",
                items = listOf(
                    AboutItemUiModel(
                        title = "Item 2.1",
                        description = "Hello",
                        type = Type.Asset("path/to/asset"),
                        icon = ImageType.EMPTY
                    ),
                )
            )
        )

        AboutScreenContent(menus = menus, onEvent = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun AboutMenuItemPreview() {
    FTEBooksTheme {
        AboutMenuItem(
            title = "Sample Menu Item",
            onClick = {},
            icon = ImageType.EMPTY,
            description = "Hello"
        )
    }
}

@Immutable
data class AboutUiModel(
    val title: String,
    val items: List<AboutItemUiModel>
)

@Immutable
data class AboutItemUiModel(
    val icon: ImageType,
    val title: String,
    val description: String,
    val type: Type
)

@Immutable
sealed class Type {
    data class Url(val url: String) : Type()
    data class Asset(val path: String) : Type()
    object None : Type()
}
