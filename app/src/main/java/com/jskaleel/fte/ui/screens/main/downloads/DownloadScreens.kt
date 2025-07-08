package com.jskaleel.fte.ui.screens.main.downloads

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.jskaleel.fte.R
import com.jskaleel.fte.core.model.ImageType
import com.jskaleel.fte.ui.screens.common.components.BookItem
import com.jskaleel.fte.ui.theme.dimension

@Composable
fun DownloadScreenContent(
    event: (DownloadEvent) -> Unit,
    books: List<BookUiModel>
) {
    LazyColumn(
        contentPadding = PaddingValues(vertical = MaterialTheme.dimension.small),
        modifier = Modifier.clipToBounds()
    ) {
        items(
            items = books,
            key = { it.id }
        ) { book ->
            BookItem(
                onOpenClick = {
                    event(DownloadEvent.OnBookClick(bookId = book.id))
                },
                image = book.image,
                title = book.title,
                author = book.author,
                category = book.category,
                downloaded = book.downloaded,
            )
            HorizontalDivider(thickness = (0.8).dp)
        }
    }
}

@Composable
fun DownloadEmptyScreen() {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.empty_download))
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever,
        reverseOnRepeat = true,
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        LottieAnimation(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f),
            composition = composition,
            progress = { progress },
        )
        Text(
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            text = stringResource(R.string.no_downloads),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        )
    }
}

@Immutable
data class BookUiModel(
    val title: String,
    val id: String,
    val author: String,
    val category: String,
    val image: ImageType,
    val downloaded: Boolean,
)
